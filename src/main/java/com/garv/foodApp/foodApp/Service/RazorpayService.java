package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.PaymentDTO;
import com.garv.foodApp.foodApp.DTO.RazorpayOrderRequest;
import com.garv.foodApp.foodApp.DTO.RazorpayOrderResponse;
import com.garv.foodApp.foodApp.DTO.RazorpayPaymentVerification;
import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Entity.Payment;
import com.garv.foodApp.foodApp.Repository.OrdersRepository;
import com.garv.foodApp.foodApp.Repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final OrdersRepository ordersRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.currency}")
    private String currency;

    @Value("${razorpay.company.name}")
    private String companyName;

    /**
     * Create Razorpay order for payment
     */
    @Transactional
    public RazorpayOrderResponse createRazorpayOrder(RazorpayOrderRequest request) throws RazorpayException {
        Orders order = ordersRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + request.getOrderId()));

        // Create Razorpay order
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (request.getAmount() * 100)); // Amount in paise
        orderRequest.put("currency", request.getCurrency() != null ? request.getCurrency() : currency);
        orderRequest.put("receipt", "ORDER_" + request.getOrderId());

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        // Create payment record in database
        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .paymentMethod("RAZORPAY")
                .paymentStatus("PENDING")
                .paymentGateway("RAZORPAY")
                .transactionId(razorpayOrder.get("id"))
                .paymentDate(LocalDateTime.now())
                .paymentDetails(razorpayOrder.toString())
                .build();

        paymentRepository.save(payment);

        return RazorpayOrderResponse.builder()
                .razorpayOrderId(razorpayOrder.get("id"))
                .amount(request.getAmount())
                .currency(razorpayOrder.get("currency"))
                .receipt(razorpayOrder.get("receipt"))
                .status(razorpayOrder.get("status"))
                .orderId(request.getOrderId())
                .keyId(razorpayKeyId)
                .build();
    }

    /**
     * Verify Razorpay payment signature
     */
    @Transactional
    public PaymentDTO verifyPayment(RazorpayPaymentVerification verification) throws RazorpayException {
        // Verify signature
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", verification.getRazorpayOrderId());
        options.put("razorpay_payment_id", verification.getRazorpayPaymentId());
        options.put("razorpay_signature", verification.getRazorpaySignature());

        boolean isValidSignature = Utils.verifyPaymentSignature(options, razorpayKeyId);

        if (!isValidSignature) {
            throw new RuntimeException("Invalid payment signature");
        }

        // Update payment status
        Payment payment = paymentRepository.findByTransactionId(verification.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found for order id: " + verification.getRazorpayOrderId()));

        payment.setPaymentStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());
        
        // Update payment details with payment ID
        JSONObject paymentDetails = new JSONObject(payment.getPaymentDetails() != null ? payment.getPaymentDetails() : "{}");
        paymentDetails.put("razorpay_payment_id", verification.getRazorpayPaymentId());
        paymentDetails.put("razorpay_signature", verification.getRazorpaySignature());
        payment.setPaymentDetails(paymentDetails.toString());

        Payment updatedPayment = paymentRepository.save(payment);

        // Update order status
        Orders order = updatedPayment.getOrder();
        if (order != null) {
            order.setStatus("CONFIRMED");
            ordersRepository.save(order);
        }

        return convertToDTO(updatedPayment);
    }

    /**
     * Handle failed payment
     */
    @Transactional
    public PaymentDTO handleFailedPayment(String razorpayOrderId, String reason) {
        Payment payment = paymentRepository.findByTransactionId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentStatus("FAILED");
        
        JSONObject paymentDetails = new JSONObject(payment.getPaymentDetails() != null ? payment.getPaymentDetails() : "{}");
        paymentDetails.put("failure_reason", reason);
        payment.setPaymentDetails(paymentDetails.toString());

        Payment updatedPayment = paymentRepository.save(payment);

        return convertToDTO(updatedPayment);
    }

    /**
     * Get payment details by Razorpay order ID
     */
    public PaymentDTO getPaymentByRazorpayOrderId(String razorpayOrderId) {
        Payment payment = paymentRepository.findByTransactionId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for Razorpay order id: " + razorpayOrderId));
        return convertToDTO(payment);
    }

    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .paymentGateway(payment.getPaymentGateway())
                .paymentDetails(payment.getPaymentDetails())
                .build();
    }
}
