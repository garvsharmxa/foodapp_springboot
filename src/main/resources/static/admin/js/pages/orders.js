// Orders Page - with placeholders for other pages
async function loadOrdersPage() {
    const container = document.getElementById('pageContainer');
    
    try {
        const orders = await api.getOrders();
        
        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h2>All Orders</h2>
                    <div style="display: flex; gap: 12px;">
                        <select id="statusFilter" onchange="filterOrders()" class="form-control">
                            <option value="">All Statuses</option>
                            <option value="PLACED">Placed</option>
                            <option value="CONFIRMED">Confirmed</option>
                            <option value="PREPARING">Preparing</option>
                            <option value="READY">Ready</option>
                            <option value="PICKED_UP">Picked Up</option>
                            <option value="COMPLETED">Completed</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>
                    </div>
                </div>
                <div class="card-body">
                    <div class="table-container" id="ordersTable">
                        ${renderOrdersTable(orders)}
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading orders:', error);
        showErrorState(container, 'Failed to load orders');
        showToast('Failed to load orders', 'error');
    }
}

function renderOrdersTable(orders) {
    if (!orders || orders.length === 0) {
        return '<p style="text-align: center; padding: 40px;">No orders found</p>';
    }
    
    return `
        <table>
            <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Customer</th>
                    <th>Restaurant</th>
                    <th>Items</th>
                    <th>Amount</th>
                    <th>Payment</th>
                    <th>Status</th>
                    <th>Date</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${orders.map(order => `
                    <tr>
                        <td><code>${order.id.substring(0, 8)}...</code></td>
                        <td>${order.user?.username || 'N/A'}</td>
                        <td>${order.restaurant?.name || 'N/A'}</td>
                        <td>${order.foodItems?.length || 0} items</td>
                        <td><strong>${formatCurrency(order.finalAmount)}</strong></td>
                        <td>${getStatusBadge(order.paymentStatus, PAYMENT_STATUS)}</td>
                        <td>${getStatusBadge(order.status, ORDER_STATUS)}</td>
                        <td>${formatDate(order.orderDate)}</td>
                        <td>
                            <button class="btn btn-sm btn-primary" onclick="updateOrderStatusModal('${order.id}', '${order.status}')">
                                Update Status
                            </button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}

function updateOrderStatusModal(orderId, currentStatus) {
    const content = `
        <form id="orderStatusForm">
            <div class="form-group">
                <label>Current Status</label>
                <p><strong>${ORDER_STATUS[currentStatus]?.label || currentStatus}</strong></p>
            </div>
            
            <div class="form-group">
                <label for="newStatus">New Status *</label>
                <select id="newStatus" required class="form-control">
                    <option value="CONFIRMED" ${currentStatus === 'CONFIRMED' ?'selected' : ''}>Confirmed</option>
                    <option value="PREPARING" ${currentStatus === 'PREPARING' ? 'selected' : ''}>Preparing</option>
                    <option value="READY" ${currentStatus === 'READY' ? 'selected' : ''}>Ready for Pickup</option>
                    <option value="PICKED_UP" ${currentStatus === 'PICKED_UP' ? 'selected' : ''}>Picked Up</option>
                    <option value="COMPLETED" ${currentStatus === 'COMPLETED' ? 'selected' : ''}>Completed</option>
                    <option value="CANCELLED" ${currentStatus === 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="remarks">Remarks (Optional)</label>
                <textarea id="remarks" class="form-control" placeholder="Add any notes about this status change"></textarea>
            </div>
        </form>
    `;
    
    showModal('Update Order Status', content, [
        { text: 'Cancel', class: 'btn', onclick: 'closeModal()' },
        { text:'Update Status', class: 'btn btn-primary', onclick: `updateOrderStatus('${orderId}')` }
    ]);
}

async function updateOrderStatus(orderId) {
    const newStatus = document.getElementById('newStatus').value;
    const remarks = document.getElementById('remarks').value;
    
    try {
        await api.updateOrderStatus(orderId, newStatus, remarks);
        showToast('Order status updated successfully!');
        closeModal();
        loadOrdersPage();
    } catch (error) {
        showToast(error.message || 'Failed to update order status', 'error');
    }
}

// Placeholder functions for other pages
async function loadLocationsPage() {
    const container = document.getElementById('pageContainer');
    container.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h2>Locations Management</h2>
                <p style="color: #64748b; margin-top: 8px;">Manage delivery locations for your food takeaway service.</p>
            </div>
            <div class="card-body">
                <p style="text-align: center; padding: 60px 20px; color: #94a3b8;">
                    <span style="font-size: 48px;">📍</span><br><br>
                    Location management will be available soon.<br>
                    You can add, edit, and manage service areas here.
                </p>
            </div>
        </div>
    `;
}

async function loadRestaurantsPage() {
    const container = document.getElementById('pageContainer');
    container.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h2>Restaurants Management</h2>
                <p style="color: #64748b; margin-top: 8px;">Manage restaurants and their details.</p>
            </div>
            <div class="card-body">
                <p style="text-align: center; padding: 60px 20px; color: #94a3b8;">
                    <span style="font-size: 48px;">🏪</span><br><br>
                    Restaurant management will be available soon.<br>
                    You can add, edit, and manage partner restaurants here.
                </p>
            </div>
        </div>
    `;
}

async function loadMenuPage() {
    const container = document.getElementById('pageContainer');
    container.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h2>Menu Items Management</h2>
                <p style="color: #64748b; margin-top: 8px;">Manage food items and menu details.</p>
            </div>
            <div class="card-body">
                <p style="text-align: center; padding: 60px 20px; color: #94a3b8;">
                    <span style="font-size: 48px;">🍕</span><br><br>
                    Menu management will be available soon.<br>
                    You can add, edit, and manage food items here.
                </p>
            </div>
        </div>
    `;
}

async function loadUsersPage() {
    const container = document.getElementById('pageContainer');
    container.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h2>Users Management</h2>
                <p style="color: #64748b; margin-top: 8px;">View and manage user accounts.</p>
            </div>
            <div class="card-body">
                <p style="text-align: center; padding: 60px 20px; color: #94a3b8;">
                    <span style="font-size: 48px;">👥</span><br><br>
                    User management will be available soon.<br>
                    You can view and manage user accounts here.
                </p>
            </div>
        </div>
    `;
}
