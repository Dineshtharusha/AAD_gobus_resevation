// ============= Navigation JS =============

const API_BASE = 'http://localhost:8080/api/v1';
const token    = localStorage.getItem('token');
const userRaw  = localStorage.getItem('user');
const loginBtn = document.querySelector('.login-btn');

// Update header login button
document.addEventListener('DOMContentLoaded', () => {
    if (token && userRaw) {
        const user = JSON.parse(userRaw);
        if (loginBtn) {
            loginBtn.innerHTML = `<i data-lucide="user-round"></i> ${user.username}`;
            if (window.lucide) lucide.createIcons();
        }
    }
});

// Handle login/dashboard button click
if (loginBtn) {
    loginBtn.addEventListener('click', () => {
        if (token && userRaw) {
            const user = JSON.parse(userRaw);
            if (user.roles && user.roles.includes('ROLE_ADMIN')) {
                window.location.href = 'admin-dashboard.html';
            } else {
                window.location.href = 'user-dashboard.html';
            }
        } else {
            window.location.href = 'login.html';
        }
    });
}
