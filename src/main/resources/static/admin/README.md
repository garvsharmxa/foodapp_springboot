# Admin Panel - Food Takeaway Application

A modern, responsive admin dashboard built with vanilla HTML, CSS, and JavaScript for managing the food takeaway platform.

## 🚀 Features

### ✅ Implemented

- **Dashboard Overview** - Statistics and recent orders at a glance
- **Coupon Management** - Full CRUD operations for discount coupons
- **Order Management** - View all orders and update order statuses
- **Authentication** - Secure login with OTP verification
- **Real-time Notifications** - Toast notifications for all actions
- **Modern UI** - Beautiful gradient designs and smooth animations

### 🔜 Coming Soon

- Locations Management
- Restaurants Management
- Menu Items Management
- Users Management

## 📂 File Structure

```
admin/
├── index.html                  # Login page
├── dashboard.html              # Main dashboard
├── css/
│   ├── login.css              # Login page styles
│   └── dashboard.css          # Dashboard styles
└── js/
    ├── config.js              # Configuration & constants
    ├── api.js                 # API service layer
    ├── utils.js               # Utility functions
    ├── login.js               # Login functionality
    ├── dashboard.js           # Dashboard controller
    └── pages/
        ├── overview.js        # Overview page
        ├── coupons.js         # Coupons management
        ├── orders.js          # Orders management
        ├── locations.js       # Locations (placeholder)
        ├── restaurants.js     # Restaurants (placeholder)
        ├── menu.js            # Menu items (placeholder)
        └── users.js           # Users (placeholder)
```

## 🎯 Access the Admin Panel

1. **Start the Application**

   ```bash
   ./mvnw spring-boot:run
   ```

2. **Open Admin Panel**

   ```
   http://localhost:3002/admin/index.html
   ```

3. **Login Credentials**
   - **Email**: `admin@foodapp.com` (or create an admin user)
   - **Password**: `admin123`

   Default admin credentials (created on startup):
   - **Username**: `admin`
   - **Email**: Create an account or use existing

## 🎨 Design Features

### Modern UI Components

- **Gradient Backgrounds** - Eye-catching purple gradients
- **Smooth Animations** - Slide-in, fade-in, and scale effects
- **Responsive Tables** - Mobile-friendly data tables
- **Modal Dialogs** - For forms and confirmations
- **Toast Notifications** - Non-intrusive success/error messages
- **Status Badges** - Color-coded order and payment statuses

### Color Scheme

- **Primary**: #6366f1 (Indigo)
- **Success**: #10b981 (Green)
- **Danger**: #ef4444 (Red)
- **Warning**: #f59e0b (Amber)
- **Dark Background**: #0f172a
- **Card Background**: #1e293b

## 🛠️ Technology Stack

- **HTML5** - Semantic markup
- **CSS3** - Modern styling with flexbox & grid
- **Vanilla JavaScript** - No frameworks, pure JS
- **Fetch API** - For REST API calls
- **LocalStorage** - For JWT token persistence

## 📱 Responsive Design

The admin panel is fully responsive and works on:

- ✅ Desktop (1920px+)
- ✅ Laptop (1366px - 1920px)
- ✅ Tablet (768px - 1365px)
- ⚠️ Mobile (< 768px) - Basic support

## 🔐 Security

- **JWT Authentication** - Secure token-based auth
- **OTP Verification** - Two-factor authentication
- **Session Management** - Automatic logout on token expiry
- **CORS Protection** - Configured in Spring Security
- **XSS Prevention** - Sanitized inputs

## 🎯 Main Features Walkthrough

### 1. Login

- Enter email and password
- Receive OTP (check console or email)
- Verify OTP to get JWT token
- Automatic redirect to dashboard

### 2. Dashboard Overview

- View key statistics (locations, restaurants, active orders, revenue)
- See recent orders
- Quick navigation to other sections

### 3. Coupon Management

- **Create Coupons**: Set code, type (percentage/fixed), value, limits
- **Edit Coupons**: Update existing coupon details
- **Delete Coupons**: Remove unwanted coupons
- **View All**: See all coupons with their usage stats

### 4. Order Management

- **View All Orders**: Complete order history
- **Filter by Status**: Filter orders by their current status
- **Update Status**: Change order status (Confirmed → Preparing → Ready → Picked Up → Completed)
- **Payment Status**: See payment statuses at a glance

## 🔧 Configuration

### API Endpoint

Update in `js/config.js`:

```javascript
const API_CONFIG = {
  BASE_URL: "http://localhost:3002/api/v1",
  TIMEOUT: 30000,
};
```

### Storage Keys

```javascript
const STORAGE_KEYS = {
  ACCESS_TOKEN: "admin_access_token",
  REFRESH_TOKEN: "admin_refresh_token",
  USER_INFO: "admin_user_info",
};
```

## 🧪 Testing

1. **Login Flow**
   - Test with valid admin credentials
   - Test OTP verification
   - Test invalid credentials

2. **Coupon CRUD**
   - Create a new coupon
   - Edit existing coupon
   - Delete coupon
   - Verify in database

3. **Order Management**
   - View orders list
   - Update order status
   - Check WebSocket notifications (if implemented)

## 📝 Notes

- The admin panel uses JWT tokens stored in localStorage
- All API calls include the Authorization header automatically
- Failed API calls (401) will redirect to login
- Toast notifications appear for all CRUD operations
- Modal dialogs are used for forms to keep the UI clean

## 🚧 Future Enhancements

- [ ] Full implementation of Locations, Restaurants, Menu, and Users pages
- [ ] Real-time order tracking with WebSocket integration
- [ ] Dark mode toggle
- [ ] Export data to CSV
- [ ] Advanced filtering and search
- [ ] Bulk operations
- [ ] Analytics dashboard with charts
- [ ] Mobile app version

## 🆘 Troubleshooting

### Can't Login

- Check if backend is running on port 3002
- Verify admin user exists in database
- Check console for API errors

### Toast Not Showing

- Check if `toastContainer` div exists in HTML
- Verify `utils.js` is loaded
- Check browser console for errors

### Modal Not Opening

- Ensure `modalContainer` div exists
- Check if utils.js `showModal()` function is loaded
- Verify button click handlers are properly bound

---

**Built with ❤️ using Vanilla JavaScript**
