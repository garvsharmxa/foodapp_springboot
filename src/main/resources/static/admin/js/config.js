// API Configuration
const API_CONFIG = {
    BASE_URL: window.location.origin + '/api/v1',
    TIMEOUT: 30000
};

// Storage Keys
const STORAGE_KEYS = {
    ACCESS_TOKEN: 'admin_access_token',
    REFRESH_TOKEN: 'admin_refresh_token',
    USER_INFO: 'admin_user_info'
};

// Order Status Mapping
const ORDER_STATUS = {
    PLACED: { label: 'Placed', class: 'info' },
    CONFIRMED: { label: 'Confirmed', class: 'success' },
    PREPARING: { label: 'Preparing', class: 'warning' },
    READY: { label: 'Ready', class: 'success' },
    PICKED_UP: { label: 'Picked Up', class: 'info' },
    COMPLETED: { label: 'Completed', class: 'success' },
    CANCELLED: { label: 'Cancelled', class: 'danger' }
};

// Payment Status Mapping
const PAYMENT_STATUS = {
    PENDING: { label: 'Pending', class: 'warning' },
    COMPLETED: { label: 'Completed', class: 'success' },
    FAILED: { label: 'Failed', class: 'danger' },
    REFUNDED: { label: 'Refunded', class: 'info' }
};

// Discount Types
const DISCOUNT_TYPES = {
    PERCENTAGE: 'Percentage',
    FIXED_AMOUNT: 'Fixed Amount'
};
