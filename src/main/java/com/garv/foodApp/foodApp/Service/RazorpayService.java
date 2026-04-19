package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.RazorpayOrderRequest;
import com.garv.foodApp.foodApp.DTO.RazorpayOrderResponse;
import com.garv.foodApp.foodApp.DTO.RazorpayPaymentVerification;
import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Entity.OrderStatus;
import com.garv.foodApp.foodApp.Entity.PaymentStatus;
import com.garv.foodApp.foodApp.Repository.OrdersRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final RazorpayClient razorpayClient;
    private final OrdersRepository ordersRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

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

        // Update order with Razorpay order ID
        String razorpayOrderId = razorpayOrder.get("id");
        order.setRazorpayOrderId(razorpayOrderId);
        order.setPaymentStatus(PaymentStatus.PENDING);
        ordersRepository.save(order);

        return RazorpayOrderResponse.builder()
                .razorpayOrderId(razorpayOrderId)
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
    public Orders verifyPayment(RazorpayPaymentVerification verification) throws RazorpayException {
        // Verify signature
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", verification.getRazorpayOrderId());
        options.put("razorpay_payment_id", verification.getRazorpayPaymentId());
        options.put("razorpay_signature", verification.getRazorpaySignature());

        boolean isValidSignature = Utils.verifyPaymentSignature(options, razorpayKeySecret);

        if (!isValidSignature) {
            throw new RuntimeException("Invalid payment signature");
        }

        // Update order status and payment details
        Orders order = ordersRepository.findByRazorpayOrderId(verification.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException(
                        "Order not found for Razorpay order id: " + verification.getRazorpayOrderId()));

        order.setRazorpayPaymentId(verification.getRazorpayPaymentId());
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setStatus(OrderStatus.CONFIRMED);

        return ordersRepository.save(order);
    }

    /**
     * Handle failed payment
     */
    @Transactional
    public Orders handleFailedPayment(String razorpayOrderId, String reason) {
        Orders order = ordersRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found for Razorpay order id: " + razorpayOrderId));

        order.setPaymentStatus(PaymentStatus.FAILED);
        // Status remains at PLACED or similar unless we want to cancel automatically

        return ordersRepository.save(order);
    }

    /**
     * Get order details by Razorpay order ID
     */
    public Orders getOrderByRazorpayOrderId(String razorpayOrderId) {
        return ordersRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found for Razorpay order id: " + razorpayOrderId));
    }
}
