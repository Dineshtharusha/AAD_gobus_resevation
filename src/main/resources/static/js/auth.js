// ============= Auth JS — connected to GoBus Spring Boot API =============

const API_BASE = 'http://localhost:8080/api/v1';

if (window.lucide) lucide.createIcons();

const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');

if (loginForm) {
    loginForm.addEventListener('submit', handleLogin);
    const guestBtn = document.getElementById('login-as-guest');
    if (guestBtn) guestBtn.addEventListener('click', handleGuestLogin);
}

if (registerForm) {
    registerForm.addEventListener('submit', handleRegister);
}

// ─── LOGIN ───────────────────────────────────────────────────────────────────
async function handleLogin(e) {
    e.preventDefault();

    const username       = document.getElementById('username').value.trim();
    const password       = document.getElementById('password').value;
    const rememberMe     = document.querySelector('input[name="remember"]')?.checked ?? false;
    const errorMsg       = document.getElementById('error-message');
    const successMsg     = document.getElementById('success-message');
    const submitBtn      = loginForm.querySelector('button[type="submit"]');

    errorMsg.style.display = 'none';
    successMsg.style.display = 'none';
    submitBtn.disabled = true;
    submitBtn.textContent = 'Signing in…';

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usernameOrEmail: username, password })
        });

        const data = await response.json();

        if (response.ok && data.success && data.data) {
            const { accessToken, userId, username: uname, email, roles } = data.data;

            // Persist session
            localStorage.setItem('token', accessToken);
            localStorage.setItem('user', JSON.stringify({ id: userId, username: uname, email, roles }));
            if (rememberMe) localStorage.setItem('remember', 'true');

            successMsg.textContent = 'Login successful! Redirecting…';
            successMsg.style.display = 'block';

            setTimeout(() => {
                if (roles && roles.includes('ROLE_ADMIN')) {
                    window.location.href = 'admin-dashboard.html';
                } else {
                    window.location.href = 'user-dashboard.html';
                }
            }, 900);
        } else {
            showError(errorMsg, data.message || 'Invalid username or password.');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Sign in';
        }
    } catch (err) {
        console.error('Login error:', err);
        showError(errorMsg, 'Cannot reach the server. Is the API running on port 8080?');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Sign in';
    }
}

// ─── GUEST ────────────────────────────────────────────────────────────────────
function handleGuestLogin() {
    localStorage.setItem('guestMode', 'true');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

// ─── REGISTER ────────────────────────────────────────────────────────────────
async function handleRegister(e) {
    e.preventDefault();

    const username        = document.getElementById('username').value.trim();
    const fullName        = document.getElementById('fullName').value.trim();
    const email           = document.getElementById('email').value.trim();
    const phone           = document.getElementById('phone').value.trim();
    const password        = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const errorMsg        = document.getElementById('error-message');
    const successMsg      = document.getElementById('success-message');
    const submitBtn       = registerForm.querySelector('button[type="submit"]');

    errorMsg.style.display = 'none';
    successMsg.style.display = 'none';

    if (password !== confirmPassword) {
        return showError(errorMsg, 'Passwords do not match.');
    }
    if (password.length < 6) {
        return showError(errorMsg, 'Password must be at least 6 characters.');
    }

    submitBtn.disabled = true;
    submitBtn.textContent = 'Creating account…';

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password, fullName, phone })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            successMsg.textContent = 'Account created! Redirecting to login…';
            successMsg.style.display = 'block';
            setTimeout(() => window.location.href = 'login.html', 1500);
        } else {
            showError(errorMsg, data.message || 'Registration failed. Please try again.');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Create account';
        }
    } catch (err) {
        console.error('Register error:', err);
        showError(errorMsg, 'Cannot reach the server. Is the API running on port 8080?');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Create account';
    }
}

// ─── HELPERS ─────────────────────────────────────────────────────────────────
function showError(el, msg) {
    el.textContent = msg;
    el.style.display = 'block';
}

// Redirect already-logged-in users away from auth pages
window.addEventListener('load', () => {
    const token = localStorage.getItem('token');
    const user  = localStorage.getItem('user');
    if (token && user && (loginForm || registerForm)) {
        const parsed = JSON.parse(user);
        if (parsed.roles && parsed.roles.includes('ROLE_ADMIN')) {
            window.location.href = 'admin-dashboard.html';
        } else {
            window.location.href = 'user-dashboard.html';
        }
    }
});
