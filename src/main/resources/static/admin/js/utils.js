// Utility Functions

// Toast notifications
function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <span>${getToastIcon(type)}</span>
        <span>${message}</span>
    `;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function getToastIcon(type) {
    const icons = {
        success: '✅',
        error: '❌',
        warning: '⚠️',
        info: 'ℹ️'
    };
    return icons[type] || icons.info;
}

// Modal
function showModal(title, content, buttons = []) {
    const modalContainer = document.getElementById('modalContainer');
    
    const buttonsHTML = buttons.map(btn => `
        <button class="btn ${btn.class || 'btn-primary'}" onclick="${btn.onclick}">
            ${btn.text}
        </button>
    `).join('');
    
    modalContainer.innerHTML = `
        <div class="modal-overlay" onclick="closeModal(event)">
            <div class="modal" onclick="event.stopPropagation()">
                <div class="modal-header">
                    <h3>${title}</h3>
                    <button class="modal-close" onclick="closeModal()">×</button>
                </div>
                <div class="modal-body">
                    ${content}
                </div>
                ${buttons.length > 0 ? `
                    <div class="modal-footer">
                        ${buttonsHTML}
                    </div>
                ` : ''}
            </div>
        </div>
    `;
}

function closeModal(event) {
    if (!event || event.target.classList.contains('modal-overlay')) {
        document.getElementById('modalContainer').innerHTML = '';
    }
}

// Confirm dialog
function confirm(message, onConfirm) {
    showModal('Confirm Action', `<p>${message}</p>`, [
        {
            text: 'Cancel',
            class: 'btn',
            onclick: 'closeModal()'
        },
        {
            text: 'Confirm',
            class: 'btn btn-danger',
            onclick: `closeModal(); (${onConfirm})()`
        }
    ]);
}

// Date formatting
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Currency formatting
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR'
    }).format(amount || 0);
}

// Status badge
function getStatusBadge(status, mapping) {
    const statusInfo = mapping[status] || { label: status, class: 'info' };
    return `<span class="badge badge-${statusInfo.class}">${statusInfo.label}</span>`;
}

// Loading state
function showLoading(element) {
    element.innerHTML = `
        <div style="text-align: center; padding: 40px;">
            <div class="loader" style="margin: 0 auto;"></div>
            <p style="margin-top: 16px; color: #94a3b8;">Loading...</p>
        </div>
    `;
}

// Empty state
function showEmptyState(element, message, icon = '📭') {
    element.innerHTML = `
        <div style="text-align: center; padding: 60px 20px; color: #94a3b8;">
            <div style="font-size: 64px; margin-bottom: 16px;">${icon}</div>
            <p style="font-size: 16px;">${message}</p>
        </div>
    `;
}

// Error state
function showErrorState(element, message) {
    element.innerHTML = `
        <div style="text-align: center; padding: 60px 20px; color: #ef4444;">
            <div style="font-size: 64px; margin-bottom: 16px;">⚠️</div>
            <p style="font-size: 16px;">${message}</p>
        </div>
    `;
}

// Generate UUID (for client-side use)
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        const v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}
