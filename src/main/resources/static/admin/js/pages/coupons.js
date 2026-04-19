// Coupons Page
async function loadCouponsPage() {
    const container = document.getElementById('pageContainer');
    
    try {
        const coupons = await api.getCoupons();
        
        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h2>All Coupons</h2>
                    <button class="btn btn-primary" onclick="showAddCouponModal()">
                        ➕ Add New Coupon
                    </button>
                </div>
                <div class="card-body">
                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Code</th>
                                    <th>Description</th>
                                    <th>Type</th>
                                    <th>Value</th>
                                    <th>Min Order</th>
                                    <th>Valid Till</th>
                                    <th>Usage</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${coupons && coupons.length > 0 ? coupons.map(coupon => `
                                    <tr>
                                        <td><strong>${coupon.code}</strong></td>
                                        <td>${coupon.description}</td>
                                        <td>${DISCOUNT_TYPES[coupon.discountType]}</td>
                                        <td>${coupon.discountType === 'PERCENTAGE' ? coupon.discountValue + '%' : formatCurrency(coupon.discountValue)}</td>
                                        <td>${formatCurrency(coupon.minimumOrderValue || 0)}</td>
                                        <td>${formatDate(coupon.validTill)}</td>
                                        <td>${coupon.usedCount || 0} / ${coupon.usageLimit || '∞'}</td>
                                        <td>
                                            <span class="badge badge-${coupon.isActive ? 'success' : 'danger'}">
                                                ${coupon.isActive ? 'Active' : 'Inactive'}
                                            </span>
                                        </td>
                                        <td>
                                            <button class="btn btn-sm btn-primary" onclick='editCoupon(${JSON.stringify(coupon)})'>
                                                ✏️
                                            </button>
                                            <button class="btn btn-sm btn-danger" onclick="deleteCoupon('${coupon.id}')">
                                                🗑️
                                            </button>
                                        </td>
                                    </tr>
                                `).join('') : '<tr><td colspan="9" style="text-align: center; padding: 40px;">No coupons found. Create your first coupon!</td></tr>'}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading coupons:', error);
        showErrorState(container, 'Failed to load coupons');
        showToast('Failed to load coupons', 'error');
    }
}

function showAddCouponModal() {
    const content = `
        <form id="couponForm" class="form-grid">
            <div class="form-group">
                <label for="code">Coupon Code *</label>
                <input type="text" id="code" required placeholder="e.g., SAVE20">
            </div>
            
            <div class="form-group">
                <label for="discountType">Discount Type *</label>
                <select id="discountType" required>
                    <option value="PERCENTAGE">Percentage</option>
                    <option value="FIXED_AMOUNT">Fixed Amount</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="discountValue">Discount Value *</label>
                <input type="number" id="discountValue" required min="0" step="0.01" placeholder="e.g., 20">
            </div>
            
            <div class="form-group">
                <label for="minimumOrderValue">Minimum Order Value</label>
                <input type="number" id="minimumOrderValue" min="0" step="0.01" placeholder="e.g., 500">
            </div>
            
            <div class="form-group">
                <label for="maxDiscountAmount">Max Discount Amount</label>
                <input type="number" id="maxDiscountAmount" min="0" step="0.01" placeholder="e.g., 200">
            </div>
            
            <div class="form-group">
                <label for="usageLimit">Usage Limit</label>
                <input type="number" id="usageLimit" min="1" placeholder="e.g., 100">
            </div>
            
            <div class="form-group">
                <label for="validFrom">Valid From *</label>
                <input type="datetime-local" id="validFrom" required>
            </div>
            
            <div class="form-group">
                <label for="validTill">Valid Till *</label>
                <input type="datetime-local" id="validTill" required>
            </div>
            
            <div class="form-group" style="grid-column: 1 / -1;">
                <label for="description">Description *</label>
                <textarea id="description" required placeholder="e.g., Get 20% off on orders above ₹500"></textarea>
            </div>
        </form>
    `;
    
    showModal('Add New Coupon', content, [
        { text: 'Cancel', class: 'btn', onclick: 'closeModal()' },
        { text: 'Create Coupon', class: 'btn btn-primary', onclick: 'saveCoupon()' }
    ]);
}

async function saveCoupon(couponId = null) {
    const form = document.getElementById('couponForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const couponData = {
        code: document.getElementById('code').value.toUpperCase(),
        description: document.getElementById('description').value,
        discountType: document.getElementById('discountType').value,
        discountValue: parseFloat(document.getElementById('discountValue').value),
        minimumOrderValue: parseFloat(document.getElementById('minimumOrderValue').value) || 0,
        maxDiscountAmount: parseFloat(document.getElementById('maxDiscountAmount').value) || null,
        usageLimit: parseInt(document.getElementById('usageLimit').value) || null,
        validFrom: document.getElementById('validFrom').value,
        validTill: document.getElementById('validTill').value,
        isActive: true
    };
    
    try {
        if (couponId) {
            await api.updateCoupon(couponId, couponData);
            showToast('Coupon updated successfully!');
        } else {
            await api.createCoupon(couponData);
            showToast('Coupon created successfully!');
        }
        closeModal();
        loadCouponsPage();
    } catch (error) {
        showToast(error.message || 'Failed to save coupon', 'error');
    }
}

function editCoupon(coupon) {
    showAddCouponModal();
    setTimeout(() => {
        document.getElementById('code').value = coupon.code;
        document.getElementById('description').value = coupon.description;
        document.getElementById('discountType').value = coupon.discountType;
        document.getElementById('discountValue').value = coupon.discountValue;
        document.getElementById('minimumOrderValue').value = coupon.minimumOrderValue || '';
        document.getElementById('maxDiscountAmount').value = coupon.maxDiscountAmount || '';
        document.getElementById('usageLimit').value = coupon.usageLimit || '';
        document.getElementById('validFrom').value = coupon.validFrom?.substring(0, 16) || '';
        document.getElementById('validTill').value = coupon.validTill?.substring(0, 16) || '';
        
        const buttons = document.querySelector('.modal-footer');
        buttons.innerHTML = `
            <button class="btn" onclick="closeModal()">Cancel</button>
            <button class="btn btn-primary" onclick="saveCoupon('${coupon.id}')">Update Coupon</button>
        `;
    }, 100);
}

async function deleteCoupon(id) {
    if (confirm('Are you sure you want to delete this coupon?', async () => {
        try {
            await api.deleteCoupon(id);
            showToast('Coupon deleted successfully!');
            loadCouponsPage();
        } catch (error) {
            showToast(error.message || 'Failed to delete coupon', 'error');
        }
    }));
}
