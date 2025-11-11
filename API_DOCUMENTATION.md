# Food App API Documentation

## Base URL
```
http://localhost:3001/api/v1
```

## Authentication

All protected endpoints require JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

### Login
```http
POST /authenticate
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 1. Restaurant APIs

### Get All Restaurants
```http
GET /restaurants
Authorization: Bearer <token>
Roles: ADMIN, RESTAURANT_OWNER

Response:
[
  {
    "id": 1,
    "name": "Pizza Palace",
    "address": "123 Main St",
    "phone": "+1234567890",
    "rating": 4.5,
    "ownerName": "John Doe",
    "deliveryRadius": 5.0,
    "minimumOrderValue": 10.0,
    "imageUrl": "https://example.com/image.jpg",
    "email": "pizza@example.com",
    "isOpen": true,
    "menu": [...]
  }
]
```

### Create Restaurant
```http
POST /restaurants
Authorization: Bearer <token>
Roles: ADMIN
Content-Type: application/json

{
  "name": "New Restaurant",
  "address": "456 Oak Ave",
  "phone": "+1234567890",
  "ownerName": "Jane Smith",
  "deliveryRadius": 10.0,
  "minimumOrderValue": 15.0,
  "email": "restaurant@example.com",
  "isOpen": true
}
```

## 2. Food Items APIs

### Get All Food Items
```http
GET /food
Authorization: Bearer <token>
Roles: ADMIN, RESTAURANT_OWNER

Response:
[
  {
    "id": 1,
    "name": "Margherita Pizza",
    "price": 12.99,
    "restaurantId": 1,
    "restaurantName": "Pizza Palace"
  }
]
```

### Create Food Item
```http
POST /food
Authorization: Bearer <token>
Roles: ADMIN
Content-Type: application/json

{
  "name": "Pepperoni Pizza",
  "price": 14.99,
  "restaurantId": 1
}
```

## 3. Cart APIs

### Get Cart
```http
GET /cart/{customerId}
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response:
{
  "id": 1,
  "customerId": 1,
  "totalAmount": 27.98,
  "items": [
    {
      "id": 1,
      "foodItemId": 1,
      "foodItemName": "Margherita Pizza",
      "quantity": 2,
      "price": 25.98
    }
  ]
}
```

### Add Item to Cart
```http
POST /cart/{customerId}/items?foodItemId=1&quantity=2
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: Updated cart
```

### Update Item Quantity
```http
PATCH /cart/{customerId}/items/{cartItemId}?quantity=3
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: Updated cart
```

### Remove Item from Cart
```http
DELETE /cart/{customerId}/items/{cartItemId}
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: Updated cart
```

### Clear Cart
```http
DELETE /cart/{customerId}
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: 204 No Content
```

## 4. Order APIs

### Create Order
```http
POST /orders
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN
Content-Type: application/json

{
  "customerId": 1,
  "restaurantId": 1,
  "foodItemIds": [1, 2, 3],
  "deliveryAddress": "789 Elm St, Apt 4B",
  "specialInstructions": "Ring doorbell twice",
  "totalAmount": 45.50
}

Response:
{
  "id": 1,
  "customerId": 1,
  "restaurantId": 1,
  "status": "PENDING",
  "totalAmount": 45.50,
  "orderDate": "2025-11-11T10:30:00",
  "deliveryAddress": "789 Elm St, Apt 4B",
  "specialInstructions": "Ring doorbell twice"
}
```

### Get Order by ID
```http
GET /orders/{id}
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: Order details
```

### Get Orders by Customer
```http
GET /orders/customer/{customerId}
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: Array of orders
```

### Update Order Status
```http
PATCH /orders/{id}
Authorization: Bearer <token>
Roles: ADMIN
Content-Type: application/json

{
  "status": "CONFIRMED"
}

Response: Updated order
```

## 5. Payment APIs

### Initiate Payment
```http
POST /payments
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN
Content-Type: application/json

{
  "orderId": 1,
  "amount": 45.50,
  "paymentMethod": "CREDIT_CARD",
  "paymentGateway": "RAZORPAY"
}

Response:
{
  "id": 1,
  "orderId": 1,
  "amount": 45.50,
  "paymentMethod": "CREDIT_CARD",
  "paymentStatus": "PENDING",
  "transactionId": "TXN12345678",
  "paymentDate": "2025-11-11T10:35:00",
  "paymentGateway": "RAZORPAY"
}
```

### Confirm Payment
```http
PATCH /payments/{paymentId}/confirm?status=COMPLETED
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: Updated payment
```

### Get Payment by Order ID
```http
GET /payments/order/{orderId}
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: Payment details
```

## 6. Razorpay Integration APIs

### Create Razorpay Order
```http
POST /razorpay/create-order
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN
Content-Type: application/json

{
  "orderId": 1,
  "amount": 45.50,
  "currency": "INR",
  "receipt": "ORDER_1"
}

Response:
{
  "razorpayOrderId": "order_ABC123XYZ",
  "amount": 45.50,
  "currency": "INR",
  "receipt": "ORDER_1",
  "status": "created",
  "orderId": 1,
  "keyId": "rzp_test_1234567890"
}
```

### Verify Razorpay Payment
```http
POST /razorpay/verify-payment
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN
Content-Type: application/json

{
  "razorpayOrderId": "order_ABC123XYZ",
  "razorpayPaymentId": "pay_XYZ789ABC",
  "razorpaySignature": "signature_hash_string",
  "orderId": 1
}

Response:
{
  "id": 1,
  "orderId": 1,
  "amount": 45.50,
  "paymentStatus": "COMPLETED",
  "transactionId": "order_ABC123XYZ",
  "paymentDate": "2025-11-11T10:40:00"
}
```

### Handle Payment Failure
```http
POST /razorpay/payment-failed?razorpayOrderId=order_ABC123XYZ&reason=Insufficient+funds
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: Updated payment with FAILED status
```

## 7. Delivery APIs

### Create Delivery
```http
POST /deliveries
Authorization: Bearer <token>
Roles: ADMIN
Content-Type: application/json

{
  "orderId": 1,
  "deliveryAddress": "789 Elm St, Apt 4B",
  "deliveryInstructions": "Ring doorbell twice",
  "deliveryFee": 5.0,
  "estimatedDeliveryTime": 30
}

Response:
{
  "id": 1,
  "orderId": 1,
  "deliveryStatus": "ASSIGNED",
  "deliveryAddress": "789 Elm St, Apt 4B",
  "deliveryFee": 5.0,
  "estimatedDeliveryTime": 30,
  "assignedAt": "2025-11-11T10:45:00"
}
```

### Update Delivery Status
```http
PATCH /deliveries/{deliveryId}/status?status=PICKED_UP
Authorization: Bearer <token>
Roles: DELIVERY_PERSON, ADMIN

Response: Updated delivery
```

### Assign Delivery Person
```http
PATCH /deliveries/{deliveryId}/assign/{deliveryPersonId}
Authorization: Bearer <token>
Roles: ADMIN

Response: Updated delivery with assigned person
```

### Get Delivery by Order ID
```http
GET /deliveries/order/{orderId}
Authorization: Bearer <token>
Roles: CUSTOMER, DELIVERY_PERSON, ADMIN

Response: Delivery details
```

### Get Deliveries by Delivery Person
```http
GET /deliveries/delivery-person/{deliveryPersonId}
Authorization: Bearer <token>
Roles: DELIVERY_PERSON, ADMIN

Response: Array of deliveries
```

## 8. Delivery Person APIs

### Create Delivery Person
```http
POST /delivery-persons
Authorization: Bearer <token>
Roles: ADMIN
Content-Type: application/json

{
  "name": "Mike Johnson",
  "email": "mike@example.com",
  "phone": "+1234567890",
  "vehicleType": "BIKE",
  "vehicleNumber": "ABC123"
}

Response:
{
  "id": 1,
  "name": "Mike Johnson",
  "email": "mike@example.com",
  "phone": "+1234567890",
  "vehicleType": "BIKE",
  "vehicleNumber": "ABC123",
  "isAvailable": true,
  "rating": 5.0,
  "totalDeliveries": 0
}
```

### Update Availability
```http
PATCH /delivery-persons/{id}/availability?isAvailable=false
Authorization: Bearer <token>
Roles: DELIVERY_PERSON, ADMIN

Response: 200 OK
```

### Get Available Delivery Persons
```http
GET /delivery-persons/available
Authorization: Bearer <token>
Roles: ADMIN

Response: Array of available delivery persons
```

## 9. Review APIs

### Create Review
```http
POST /reviews
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN
Content-Type: application/json

{
  "customerId": 1,
  "restaurantId": 1,
  "orderId": 1,
  "rating": 5,
  "comment": "Excellent food and fast delivery!"
}

Response:
{
  "id": 1,
  "customerId": 1,
  "customerName": "John Customer",
  "restaurantId": 1,
  "restaurantName": "Pizza Palace",
  "orderId": 1,
  "rating": 5,
  "comment": "Excellent food and fast delivery!",
  "createdAt": "2025-11-11T11:00:00"
}
```

### Update Review
```http
PUT /reviews/{reviewId}
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN
Content-Type: application/json

{
  "rating": 4,
  "comment": "Updated: Good food but delivery was slow"
}

Response: Updated review
```

### Get Restaurant Reviews
```http
GET /reviews/restaurant/{restaurantId}
Authorization: Bearer <token>
Roles: All authenticated users

Response: Array of reviews
```

### Get Customer Reviews
```http
GET /reviews/customer/{customerId}
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: Array of reviews
```

### Delete Review
```http
DELETE /reviews/{reviewId}
Authorization: Bearer <token>
Roles: CUSTOMER, ADMIN

Response: 204 No Content
```

## Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `204 No Content` - Request successful, no content to return
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Missing or invalid authentication
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Order Status Flow

```
PENDING → CONFIRMED → PREPARING → READY → OUT_FOR_DELIVERY → DELIVERED
                                                        ↓
                                                   CANCELLED
```

## Delivery Status Flow

```
ASSIGNED → PICKED_UP → IN_TRANSIT → DELIVERED
                              ↓
                         CANCELLED
```

## Payment Status Flow

```
PENDING → COMPLETED
    ↓
  FAILED → REFUNDED
```

## Testing with cURL

### Example: Create Order
```bash
curl -X POST http://localhost:3001/api/v1/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "restaurantId": 1,
    "foodItemIds": [1, 2],
    "deliveryAddress": "123 Test St",
    "totalAmount": 25.50
  }'
```

### Example: Initiate Razorpay Payment
```bash
curl -X POST http://localhost:3001/api/v1/razorpay/create-order \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "amount": 25.50,
    "currency": "INR"
  }'
```

## WebSocket Connection

Connect to WebSocket for real-time order updates:

```javascript
const socket = new SockJS('http://localhost:3001/api/v1/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
  stompClient.subscribe('/topic/order-status', function(message) {
    console.log('Order update:', JSON.parse(message.body));
  });
});
```

## Error Response Format

```json
{
  "timestamp": "2025-11-11T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid order data",
  "path": "/api/v1/orders"
}
```
