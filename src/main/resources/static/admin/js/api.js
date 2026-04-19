// API Service
class API {
    constructor() {
        this.baseURL = API_CONFIG.BASE_URL;
    }

    getAuthHeaders() {
        const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        };
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const headers = options.headers || this.getAuthHeaders();

        try {
            const response = await fetch(url, {
                ...options,
                headers
            });

            if (response.status === 401 || response.status === 403) {
                // Unauthorized - redirect to login
                this.logout();
                return null;
            }

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Request failed');
            }

            // Handle empty responses
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            return null;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // Auth
    async login(email, password) {
        return this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
    }

    logout() {
        localStorage.clear();
        window.location.href = '/admin/index.html';
    }

    // Locations
    async getLocations() {
        return this.request('/user/locations');
    }

    async createLocation(location) {
        return this.request('/admin/locations', {
            method: 'POST',
            body: JSON.stringify(location)
        });
    }

    async updateLocation(id, location) {
        return this.request(`/admin/locations/${id}`, {
            method: 'PUT',
            body: JSON.stringify(location)
        });
    }

    async deleteLocation(id) {
        return this.request(`/admin/locations/${id}`, {
            method: 'DELETE'
        });
    }

    // Restaurants
    async getRestaurants() {
        return this.request('/admin/restaurants');
    }

    async getRestaurantsByLocation(locationId) {
        return this.request(`/user/vendors?locationId=${locationId}`);
    }

    async createRestaurant(restaurant) {
        return this.request('/admin/restaurants', {
            method: 'POST',
            body: JSON.stringify(restaurant)
        });
    }

    async updateRestaurant(id, restaurant) {
        return this.request(`/admin/restaurants/${id}`, {
            method: 'PUT',
            body: JSON.stringify(restaurant)
        });
    }

    async deleteRestaurant(id) {
        return this.request(`/admin/restaurants/${id}`, {
            method: 'DELETE'
        });
    }

    // Food Items
    async getFoodItems(restaurantId) {
        return this.request(`/user/vendors/${restaurantId}/menu`);
    }

    async createFoodItem(foodItem) {
        return this.request('/admin/food-items', {
            method: 'POST',
            body: JSON.stringify(foodItem)
        });
    }

    async updateFoodItem(id, foodItem) {
        return this.request(`/admin/food-items/${id}`, {
            method: 'PUT',
            body: JSON.stringify(foodItem)
        });
    }

    async deleteFoodItem(id) {
        return this.request(`/admin/food-items/${id}`, {
            method: 'DELETE'
        });
    }

    // Coupons
    async getCoupons() {
        return this.request('/admin/coupons');
    }

    async createCoupon(coupon) {
        return this.request('/admin/coupons', {
            method: 'POST',
            body: JSON.stringify(coupon)
        });
    }

    async updateCoupon(id, coupon) {
        return this.request(`/admin/coupons/${id}`, {
            method: 'PUT',
            body: JSON.stringify(coupon)
        });
    }

    async deleteCoupon(id) {
        return this.request(`/admin/coupons/${id}`, {
            method: 'DELETE'
        });
    }

    // Orders
    async getOrders() {
        return this.request('/admin/orders');
    }

    async updateOrderStatus(orderId, status, remarks) {
        return this.request(`/merchant/orders/${orderId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ orderId, status, remarks })
        });
    }

    // Users
    async getUsers() {
        return this.request('/admin/users');
    }
}

const api = new API();
