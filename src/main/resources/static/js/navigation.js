// ============= Navigation JS =============

window.API_BASE = window.API_BASE || 'http://localhost:8080/api/v1';
var API_BASE = window.API_BASE;
const token    = localStorage.getItem('token');
const userRaw  = localStorage.getItem('user');
const loginBtn = document.querySelector('.login-btn');

// Update header login button
document.addEventListener('DOMContentLoaded', () => {
    if (token && userRaw) {
        try {
            const user = JSON.parse(userRaw);
            if (loginBtn) {
                loginBtn.innerHTML = `<i data-lucide="user-round"></i> ${user.fullName || user.username || 'User'}`;
                if (window.lucide) lucide.createIcons();
            }
        } catch (e) {
            console.error('Error reading user session:', e);
        }
    }
});

// Handle login/dashboard button click
if (loginBtn) {
    loginBtn.addEventListener('click', () => {
        if (token && userRaw) {
            try {
                const user = JSON.parse(userRaw);
                if (user.roles && user.roles.includes('ROLE_ADMIN')) {
                    window.location.href = 'admin-dashboard.html';
                } else if (user.roles && user.roles.includes('ROLE_BUS_OWNER')) {
                    window.location.href = 'owner-dashboard.html';
                } else {
                    window.location.href = 'user-dashboard.html';
                }
            } catch (e) {
                window.location.href = 'user-dashboard.html';
            }
        } else {
            window.location.href = 'login.html';
        }
    });
}
