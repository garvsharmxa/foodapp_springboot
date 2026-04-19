// Overview Page
async function loadOverviewPage() {
    const container = document.getElementById('pageContainer');
    
    try {
        // Fetch data for stats
        const [locations, restaurants, orders, coupons] = await Promise.all([
            api.getLocations(),
            api.getRestaurants(),
            api.getOrders(),
            api.getCoupons()
        ]);
        
        const activeOrders = orders?.filter(o => o.status !== 'COMPLETED' && o.status !== 'CANCELLED') || [];
        const todayOrders = orders?.filter(o => {
            const orderDate = new Date(o.orderDate);
            const today = new Date();
            return orderDate.toDateString() === today.toDateString();
        }) || [];
        
        const todayRevenue = todayOrders.reduce((sum, o) => sum + (o.finalAmount || 0), 0);
        
        container.innerHTML = `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-card-header">
                        <div class="stat-icon primary">📍</div>
                    </div>
                    <div class="stat-value">${locations?.length || 0}</div>
                    <div class="stat-label">Total Locations</div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-card-header">
                        <div class="stat-icon success">🏪</div>
                    </div>
                    <div class="stat-value">${restaurants?.length || 0}</div>
                    <div class="stat-label">Active Restaurants</div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-card-header">
                        <div class="stat-icon warning">📦</div>
                    </div>
                    <div class="stat-value">${activeOrders.length}</div>
                    <div class="stat-label">Active Orders</div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-card-header">
                        <div class="stat-icon success">💰</div>
                    </div>
                    <div class="stat-value">${formatCurrency(todayRevenue)}</div>
                    <div class="stat-label">Today's Revenue</div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h2>Recent Orders</h2>
                    <button class="btn btn-primary btn-sm" onclick="loadPage('orders')">
                        View All Orders →
                    </button>
                </div>
                <div class="card-body">
                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Order ID</th>
                                    <th>Customer</th>
                                    <th>Restaurant</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th>Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${orders && orders.length > 0 ? orders.slice(0, 5).map(order => `
                                    <tr>
                                        <td><code>${order.id.substring(0, 8)}...</code></td>
                                        <td>${order.user?.username || 'N/A'}</td>
                                        <td>${order.restaurant?.name || 'N/A'}</td>
                                        <td><strong>${formatCurrency(order.finalAmount)}</strong></td>
                                        <td>${getStatusBadge(order.status, ORDER_STATUS)}</td>
                                        <td>${formatDate(order.orderDate)}</td>
                                    </tr>
                                `).join('') : '<tr><td colspan="6" style="text-align: center; padding: 40px;">No orders found</td></tr>'}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading overview:', error);
        showErrorState(container, 'Failed to load dashboard data');
        showToast('Failed to load dashboard data', 'error');
    }
}
