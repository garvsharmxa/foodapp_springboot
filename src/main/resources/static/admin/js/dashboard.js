// Dashboard Main Controller
let currentPage = 'overview';

// Check authentication
if (!localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)) {
    window.location.href = 'index.html';
}

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    // Set user info
    const userInfo = JSON.parse(localStorage.getItem(STORAGE_KEYS.USER_INFO) || '{}');
    document.getElementById('adminName').textContent = userInfo.email || 'Admin';
    
    // Setup navigation
    setupNavigation();
    
    // Setup logout
    document.getElementById('logoutBtn').addEventListener('click', function() {
        if (window.confirm('Are you sure you want to logout?')) {
            api.logout();
        }
    });
    
    // Load default page
    loadPage('overview');
});

function setupNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.dataset.page;
            
            // Update active state
            navItems.forEach(nav => nav.classList.remove('active'));
            this.classList.add('active');
            
            // Load page
            loadPage(page);
        });
    });
}

function loadPage(pageName) {
    currentPage = pageName;
    const container = document.getElementById('pageContainer');
    const pageTitle = document.getElementById('pageTitle');
    
    // Update title
    const titles = {
        overview: 'Dashboard Overview',
        locations: 'Manage Locations',
        restaurants: 'Manage Restaurants',
        menu: 'Manage Menu Items',
        coupons: 'Manage Coupons',
        orders: 'Manage Orders',
        users: 'Manage Users'
    };
    pageTitle.textContent = titles[pageName] || 'Dashboard';
    
    // Show loading
    showLoading(container);
    
    // Load page content
    try {
        window[`load${capitalize(pageName)}Page`]();
    } catch (error) {
        console.error('Error loading page:', error);
        showErrorState(container, 'Failed to load page');
    }
}

function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}
