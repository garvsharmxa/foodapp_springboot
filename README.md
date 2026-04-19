# Food App - Spring Boot Application

A comprehensive food delivery application built with Spring Boot featuring payment processing, delivery tracking, cart management, reviews, and more.

## Features

### 🔐 Security & Authentication
- JWT-based authentication
- Role-based access control (RBAC)
- Supported roles: ADMIN, CUSTOMER, RESTAURANT_OWNER, DELIVERY_PERSON
- Secure endpoints with role-specific access

### 💳 Payment Processing
- **Razorpay Integration**: Complete payment gateway integration
- Multiple payment methods: Credit Card, Debit Card, UPI, Cash on Delivery, Wallet
- Payment status tracking: PENDING, COMPLETED, FAILED, REFUNDED
- Payment verification with signature validation
- Transaction history and tracking

### 🚚 Delivery Management
- Real-time delivery status tracking
- Delivery status flow: ASSIGNED → PICKED_UP → IN_TRANSIT → DELIVERED
- Delivery person assignment and availability tracking
- Estimated delivery time calculation
- Delivery address and special instructions support

### 🛒 Shopping Cart
- Add/remove items from cart
- Update item quantities
- Automatic price calculation
- Cart persistence per customer
- Clear cart functionality

### ⭐ Reviews & Ratings
- Customer reviews for restaurants
- 1-5 star rating system
- Automatic restaurant rating calculation
- Review history per customer
- Order-linked reviews

### 🍽️ Restaurant & Food Management
- Restaurant listing with details
- Menu management (food items)
- Restaurant ratings and status (open/closed)
- Delivery radius and minimum order value
- Restaurant search and filtering

### 📦 Order Management
- Complete order lifecycle management
- Order status tracking: PENDING → CONFIRMED → PREPARING → READY → OUT_FOR_DELIVERY → DELIVERED → CANCELLED
- Order history per customer
- Special instructions and delivery preferences
- Order total calculation with delivery fees

### 👥 User Management
- Customer profiles
- Delivery person profiles
- Admin management capabilities
- User authentication and authorization

## Tech Stack

- **Framework**: Spring Boot 3.5.4
- **Java Version**: 17
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: Spring Security with JWT
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Payment Gateway**: Razorpay

## Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Cache
- Spring Data Redis
- Spring Boot Starter WebSocket
- Spring Boot Starter Actuator
- Spring Boot Starter AOP
- PostgreSQL Driver
- JWT (jjwt-api, jjwt-impl, jjwt-jackson)
- Razorpay Java SDK
- Lombok
- MapStruct
- ModelMapper

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database
- Redis server
- Razorpay account (for payment integration)

### Configuration

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/foodAppDB
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Razorpay Configuration
razorpay.key.id=your_razorpay_key_id
razorpay.key.secret=your_razorpay_key_secret
razorpay.currency=INR
razorpay.company.name=Food App
```

### Build and Run

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:3001/api/v1`

## API Endpoints

### Authentication
- `POST /api/v1/authenticate` - Login and get JWT token

### Restaurants
- `GET /api/v1/restaurants` - Get all restaurants (Admin/Restaurant Owner)
- `POST /api/v1/restaurants` - Create restaurant (Admin)
- `GET /api/v1/restaurants/{id}` - Get restaurant by ID
- `PUT /api/v1/restaurants/{id}` - Update restaurant (Admin)
- `DELETE /api/v1/restaurants/{id}` - Delete restaurant (Admin)

### Food Items
- `GET /api/v1/food` - Get all food items (Admin/Restaurant Owner)
- `POST /api/v1/food` - Create food item (Admin)
- `GET /api/v1/food/{id}` - Get food item by ID
- `PUT /api/v1/food/{id}` - Update food item (Admin)
- `DELETE /api/v1/food/{id}` - Delete food item (Admin)

### Cart
- `GET /api/v1/cart/{customerId}` - Get customer cart (Customer)
- `POST /api/v1/cart/{customerId}/items` - Add item to cart (Customer)
- `DELETE /api/v1/cart/{customerId}/items/{cartItemId}` - Remove item from cart (Customer)
- `PATCH /api/v1/cart/{customerId}/items/{cartItemId}` - Update item quantity (Customer)
- `DELETE /api/v1/cart/{customerId}` - Clear cart (Customer)

### Orders
- `GET /api/v1/orders` - Get all orders (Admin/Customer)
- `POST /api/v1/orders` - Create order (Customer)
- `GET /api/v1/orders/{id}` - Get order by ID
- `GET /api/v1/orders/customer/{customerId}` - Get customer orders (Customer)
- `PATCH /api/v1/orders/{id}` - Update order status (Admin)
- `DELETE /api/v1/orders/{id}` - Delete order (Admin)

### Payments
- `POST /api/v1/payments` - Initiate payment (Customer)
- `PATCH /api/v1/payments/{paymentId}/confirm` - Confirm payment
- `GET /api/v1/payments/{id}` - Get payment by ID
- `GET /api/v1/payments/order/{orderId}` - Get payment by order ID
- `GET /api/v1/payments` - Get all payments (Admin)

### Razorpay
- `POST /api/v1/razorpay/create-order` - Create Razorpay order (Customer)
- `POST /api/v1/razorpay/verify-payment` - Verify Razorpay payment (Customer)
- `POST /api/v1/razorpay/payment-failed` - Handle failed payment
- `GET /api/v1/razorpay/payment/{razorpayOrderId}` - Get payment by Razorpay order ID

### Deliveries
- `POST /api/v1/deliveries` - Create delivery (Admin)
- `PATCH /api/v1/deliveries/{deliveryId}/status` - Update delivery status (Delivery Person/Admin)
- `PATCH /api/v1/deliveries/{deliveryId}/assign/{deliveryPersonId}` - Assign delivery person (Admin)
- `GET /api/v1/deliveries/order/{orderId}` - Get delivery by order ID
- `GET /api/v1/deliveries/delivery-person/{deliveryPersonId}` - Get deliveries by delivery person
- `GET /api/v1/deliveries` - Get all deliveries (Admin)

### Delivery Persons
- `POST /api/v1/delivery-persons` - Create delivery person (Admin)
- `PUT /api/v1/delivery-persons/{id}` - Update delivery person (Admin)
- `PATCH /api/v1/delivery-persons/{id}/availability` - Update availability
- `GET /api/v1/delivery-persons` - Get all delivery persons (Admin)
- `GET /api/v1/delivery-persons/available` - Get available delivery persons
- `GET /api/v1/delivery-persons/{id}` - Get delivery person by ID
- `DELETE /api/v1/delivery-persons/{id}` - Delete delivery person (Admin)

### Reviews
- `POST /api/v1/reviews` - Create review (Customer)
- `PUT /api/v1/reviews/{reviewId}` - Update review (Customer)
- `GET /api/v1/reviews/{reviewId}` - Get review by ID
- `GET /api/v1/reviews/restaurant/{restaurantId}` - Get restaurant reviews
- `GET /api/v1/reviews/customer/{customerId}` - Get customer reviews
- `DELETE /api/v1/reviews/{reviewId}` - Delete review (Customer/Admin)

### Customers
- `GET /api/v1/customers` - Get all customers (Admin)
- `POST /api/v1/customers` - Create customer (Admin)
- `GET /api/v1/customers/{id}` - Get customer by ID (Admin)
- `PUT /api/v1/customers/{id}` - Update customer (Admin)
- `DELETE /api/v1/customers/{id}` - Delete customer (Admin)

## Payment Flow with Razorpay

1. **Create Order**: Customer creates an order in the system
2. **Initiate Payment**: Frontend calls `/razorpay/create-order` with order details
3. **Razorpay Checkout**: Use the returned `razorpayOrderId` and `keyId` to open Razorpay checkout
4. **Payment Success**: After successful payment, call `/razorpay/verify-payment` with payment details
5. **Verification**: Backend verifies the payment signature and updates order status
6. **Payment Failed**: If payment fails, call `/razorpay/payment-failed` to update status

## Database Schema

### Main Entities
- **Users**: Authentication and user management
- **Customer**: Customer profile information
- **Restaurant**: Restaurant details and menu
- **FoodItems**: Individual food items with pricing
- **Orders**: Order information and status
- **Payment**: Payment transactions and status
- **Delivery**: Delivery tracking and status
- **DeliveryPerson**: Delivery personnel information
- **Cart**: Shopping cart for customers
- **CartItem**: Individual cart items
- **Review**: Customer reviews and ratings

## Security

### Role-Based Access Control

- **ADMIN**: Full access to all endpoints
- **CUSTOMER**: Access to cart, orders, reviews, and payments
- **RESTAURANT_OWNER**: Access to restaurant and food management
- **DELIVERY_PERSON**: Access to delivery management

### JWT Authentication

All protected endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## WebSocket Support

Real-time order status updates using WebSocket at `/ws` endpoint.

## Caching

Redis caching is implemented for frequently accessed data like customer information to improve performance.

## Development

### Run Tests
```bash
./mvnw test
```

### Code Quality
The project uses:
- Lombok for reducing boilerplate code
- MapStruct for DTO mapping
- Spring AOP for cross-cutting concerns
- Hibernate for ORM

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is open source and available under the MIT License.

## Support

For issues and questions, please create an issue in the repository.
