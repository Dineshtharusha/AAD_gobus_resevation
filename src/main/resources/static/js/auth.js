// ============= Auth JS — GoBus (Email Verification & Password Reset) =============

const API_BASE = (window.location.protocol.startsWith('http') && window.location.port === '8080')
    ? `${window.location.origin}/api/v1`
    : 'http://localhost:8080/api/v1';

if (window.lucide) lucide.createIcons();

// Elements
const loginForm        = document.getElementById('login-form');
const registerForm     = document.getElementById('register-form');
const verificationCard = document.getElementById('verification-card');

let pendingVerificationEmail = '';
let signupCountdownInterval  = null;
let fpCountdownInterval      = null;

// =============================================================================
// OTP Input Box Helpers (6-digit boxes with auto-advance and paste support)
// =============================================================================
function setupOtpInputs(containerId, onComplete) {
    const container = document.getElementById(containerId);
    if (!container) return;

    const inputs = Array.from(container.querySelectorAll('.otp-box'));

    inputs.forEach((input, index) => {
        // Handle single digit input
        input.addEventListener('input', (e) => {
            const val = e.target.value.replace(/[^0-9]/g, '');
            e.target.value = val ? val[0] : '';

            if (e.target.value) {
                e.target.classList.add('filled');
                if (index < inputs.length - 1) {
                    inputs[index + 1].focus();
                }
            } else {
                e.target.classList.remove('filled');
            }

            if (onComplete && inputs.every(i => i.value.length === 1)) {
                onComplete(inputs.map(i => i.value).join(''));
            }
        });

        // Handle Backspace navigation
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && !e.target.value && index > 0) {
                inputs[index - 1].focus();
                inputs[index - 1].value = '';
                inputs[index - 1].classList.remove('filled');
            }
        });

        // Handle Paste (e.g. user pastes full 6-digit code)
        input.addEventListener('paste', (e) => {
            e.preventDefault();
            const pasteData = (e.clipboardData || window.clipboardData)
                .getData('text')
                .replace(/[^0-9]/g, '')
                .slice(0, inputs.length);

            if (pasteData) {
                pasteData.split('').forEach((char, i) => {
                    if (inputs[i]) {
                        inputs[i].value = char;
                        inputs[i].classList.add('filled');
                    }
                });
                const nextIdx = Math.min(pasteData.length, inputs.length - 1);
                inputs[nextIdx].focus();

                if (onComplete && inputs.every(i => i.value.length === 1)) {
                    onComplete(inputs.map(i => i.value).join(''));
                }
            }
        });
    });
}

function getOtpValue(containerId) {
    const container = document.getElementById(containerId);
    if (!container) return '';
    const inputs = container.querySelectorAll('.otp-box');
    return Array.from(inputs).map(i => i.value.trim()).join('');
}

function clearOtpInputs(containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;
    container.querySelectorAll('.otp-box').forEach(input => {
        input.value = '';
        input.classList.remove('filled');
    });
    const first = container.querySelector('.otp-box');
    if (first) first.focus();
}

// =============================================================================
// SIGNUP FLOW (register.html)
// =============================================================================
if (registerForm) {
    registerForm.addEventListener('submit', handleRegister);

    setupOtpInputs('signup-otp-boxes', (otp) => {
        // Auto-focus verify button or ready to submit
    });

    const verifyBtn = document.getElementById('btn-verify-signup-otp');
    if (verifyBtn) verifyBtn.addEventListener('click', handleVerifySignupOtp);

    const resendBtn = document.getElementById('btn-resend-signup-otp');
    if (resendBtn) resendBtn.addEventListener('click', handleResendSignupOtp);

    const backBtn = document.getElementById('btn-back-to-register');
    if (backBtn) backBtn.addEventListener('click', () => {
        verificationCard.style.display = 'none';
        registerForm.style.display = 'block';
        document.getElementById('auth-main-title').textContent = 'Create your account';
    });

    // Check URL parameters for direct verification link (e.g. ?email=...&code=...)
    window.addEventListener('DOMContentLoaded', () => {
        const params = new URLSearchParams(window.location.search);
        const emailParam = params.get('email');
        const codeParam = params.get('code');

        if (emailParam) {
            showSignupVerificationUI(emailParam);
            if (codeParam && codeParam.length === 6) {
                const inputs = document.querySelectorAll('#signup-otp-boxes .otp-box');
                codeParam.split('').forEach((ch, idx) => {
                    if (inputs[idx]) {
                        inputs[idx].value = ch;
                        inputs[idx].classList.add('filled');
                    }
                });
                handleVerifySignupOtp();
            }
        }
    });
}

async function handleRegister(e) {
    e.preventDefault();

    const username        = document.getElementById('username').value.trim();
    const fullName        = document.getElementById('fullName').value.trim();
    const email           = document.getElementById('email').value.trim();
    const phone           = document.getElementById('phone').value.trim();
    const password        = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const agreeTerms      = document.getElementById('terms-agree')?.checked;
    const errorMsg        = document.getElementById('error-message');
    const successMsg      = document.getElementById('success-message');
    const submitBtn       = registerForm.querySelector('button[type="submit"]');

    errorMsg.style.display = 'none';
    successMsg.style.display = 'none';

    if (!agreeTerms) {
        return showError(errorMsg, 'You must agree to the Terms & Conditions.');
    }
    if (password.length < 8) {
        return showError(errorMsg, 'Password must be at least 8 characters long.');
    }
    if (password !== confirmPassword) {
        return showError(errorMsg, 'Passwords do not match.');
    }

    submitBtn.disabled = true;
    submitBtn.innerHTML = 'Sending code…';

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password, fullName, phone })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            showSignupVerificationUI(email);
        } else {
            showError(errorMsg, data.message || 'Registration failed. Please try again.');
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i data-lucide="user-plus"></i> Create account';
            if (window.lucide) lucide.createIcons();
        }
    } catch (err) {
        console.error('Register error:', err);
        showError(errorMsg, 'Cannot reach the server. Please verify the backend is running.');
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i data-lucide="user-plus"></i> Create account';
        if (window.lucide) lucide.createIcons();
    }
}

function showSignupVerificationUI(email) {
    pendingVerificationEmail = email;
    registerForm.style.display = 'none';
    verificationCard.style.display = 'block';
    document.getElementById('auth-main-title').textContent = 'Email Verification';
    document.getElementById('verify-email-display').textContent = email;

    clearOtpInputs('signup-otp-boxes');
    startSignupResendTimer();
    if (window.lucide) lucide.createIcons();
}

async function handleVerifySignupOtp() {
    const code = getOtpValue('signup-otp-boxes');
    const errEl = document.getElementById('verify-error-message');
    const succEl = document.getElementById('verify-success-message');
    const verifyBtn = document.getElementById('btn-verify-signup-otp');

    errEl.style.display = 'none';
    succEl.style.display = 'none';

    if (code.length < 6) {
        return showError(errEl, 'Please enter the complete 6-digit verification code.');
    }

    verifyBtn.disabled = true;
    verifyBtn.innerHTML = 'Verifying…';

    try {
        const response = await fetch(`${API_BASE}/auth/verify-email`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: pendingVerificationEmail, code })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            succEl.textContent = 'Account verified successfully! Redirecting to sign in…';
            succEl.style.display = 'block';

            const userToFill = data.data?.username || data.data?.email || pendingVerificationEmail;
            setTimeout(() => {
                window.location.href = `login.html?registered=true&user=${encodeURIComponent(userToFill)}`;
            }, 1200);
        } else {
            showError(errEl, data.message || 'Verification failed. Invalid or expired code.');
            verifyBtn.disabled = false;
            verifyBtn.innerHTML = '<i data-lucide="check-circle-2"></i> Verify &amp; Activate Account';
            if (window.lucide) lucide.createIcons();
        }
    } catch (err) {
        console.error('Verify error:', err);
        showError(errEl, 'Cannot connect to server. Please try again.');
        verifyBtn.disabled = false;
        verifyBtn.innerHTML = '<i data-lucide="check-circle-2"></i> Verify &amp; Activate Account';
        if (window.lucide) lucide.createIcons();
    }
}

async function handleResendSignupOtp() {
    const resendBtn = document.getElementById('btn-resend-signup-otp');
    const errEl = document.getElementById('verify-error-message');
    const succEl = document.getElementById('verify-success-message');

    errEl.style.display = 'none';
    succEl.style.display = 'none';

    if (!pendingVerificationEmail) return;

    resendBtn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/auth/resend-verification`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: pendingVerificationEmail })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            succEl.textContent = 'New verification code sent! Check your inbox.';
            succEl.style.display = 'block';
            startSignupResendTimer();
        } else {
            showError(errEl, data.message || 'Failed to resend code.');
            resendBtn.disabled = false;
        }
    } catch (err) {
        showError(errEl, 'Could not reach server.');
        resendBtn.disabled = false;
    }
}

function startSignupResendTimer() {
    const resendBtn = document.getElementById('btn-resend-signup-otp');
    const timerContainer = document.getElementById('signup-resend-timer');
    const countdownSpan = document.getElementById('signup-countdown');

    if (!resendBtn || !timerContainer || !countdownSpan) return;

    if (signupCountdownInterval) clearInterval(signupCountdownInterval);

    resendBtn.disabled = true;
    timerContainer.style.display = 'inline';
    let timeLeft = 60;
    countdownSpan.textContent = timeLeft;

    signupCountdownInterval = setInterval(() => {
        timeLeft--;
        countdownSpan.textContent = timeLeft;
        if (timeLeft <= 0) {
            clearInterval(signupCountdownInterval);
            timerContainer.style.display = 'none';
            resendBtn.disabled = false;
        }
    }, 1000);
}

// =============================================================================
// LOGIN & FORGOT PASSWORD FLOW (login.html)
// =============================================================================
if (loginForm) {
    loginForm.addEventListener('submit', handleLogin);

    const guestBtn = document.getElementById('login-as-guest');
    if (guestBtn) guestBtn.addEventListener('click', handleGuestLogin);

    const forgotLink = document.getElementById('link-forgot-password');
    if (forgotLink) forgotLink.addEventListener('click', (e) => {
        e.preventDefault();
        openForgotPasswordModal();
    });

    const verifyNowBtn = document.getElementById('btn-verify-now');
    if (verifyNowBtn) verifyNowBtn.addEventListener('click', () => {
        const userOrEmail = document.getElementById('username').value.trim();
        window.location.href = `register.html?email=${encodeURIComponent(userOrEmail)}`;
    });

    // Check for registered param
    const params = new URLSearchParams(window.location.search);
    if (params.get('registered') === 'true') {
        const uParam = params.get('user');
        if (uParam) {
            const uField = document.getElementById('username');
            if (uField) uField.value = uParam;
        }
        const sMsg = document.getElementById('success-message');
        if (sMsg) {
            sMsg.textContent = '🎉 Account created successfully! Please enter your password to sign in.';
            sMsg.style.display = 'block';
        }
    }

    initForgotPasswordModal();
}

async function handleLogin(e) {
    e.preventDefault();

    const username       = document.getElementById('username').value.trim();
    const password       = document.getElementById('password').value;
    const rememberMe     = document.querySelector('input[name="remember"]')?.checked ?? false;
    const errorMsg       = document.getElementById('error-message');
    const successMsg     = document.getElementById('success-message');
    const unverifiedEl   = document.getElementById('unverified-banner');
    const submitBtn      = loginForm.querySelector('button[type="submit"]');

    errorMsg.style.display = 'none';
    successMsg.style.display = 'none';
    if (unverifiedEl) unverifiedEl.style.display = 'none';

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
                } else if (roles && roles.includes('ROLE_BUS_OWNER')) {
                    window.location.href = 'owner-dashboard.html';
                } else {
                    window.location.href = 'user-dashboard.html';
                }
            }, 900);
        } else {
            // Check if account is unverified
            if (response.status === 403 || (data.message && data.message.toLowerCase().includes('not verified'))) {
                if (unverifiedEl) unverifiedEl.style.display = 'block';
            } else {
                showError(errorMsg, data.message || 'Invalid username or password.');
            }
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i data-lucide="log-in"></i> Sign in';
            if (window.lucide) lucide.createIcons();
        }
    } catch (err) {
        console.error('Login error:', err);
        showError(errorMsg, 'Cannot reach the server. Is the API running on port 8080?');
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i data-lucide="log-in"></i> Sign in';
        if (window.lucide) lucide.createIcons();
    }
}

function handleGuestLogin() {
    localStorage.setItem('guestMode', 'true');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

// ─── FORGOT PASSWORD MODAL ──────────────────────────────────────────────────
let fpEmail = '';

function initForgotPasswordModal() {
    const modal       = document.getElementById('forgot-password-modal');
    const closeBtn    = document.getElementById('fp-modal-close');
    const step1Form   = document.getElementById('fp-step-1');
    const step2Form   = document.getElementById('fp-step-2');
    const resendBtn   = document.getElementById('btn-resend-fp-otp');

    if (!modal) return;

    if (closeBtn) closeBtn.addEventListener('click', closeForgotPasswordModal);

    modal.addEventListener('click', (e) => {
        if (e.target === modal) closeForgotPasswordModal();
    });

    setupOtpInputs('fp-otp-boxes');

    // Step 1: Send Reset Code
    if (step1Form) {
        step1Form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const emailInput = document.getElementById('fp-email');
            const email = emailInput.value.trim();
            const errEl = document.getElementById('fp-error-msg');
            const succEl = document.getElementById('fp-success-msg');
            const btn = document.getElementById('btn-send-reset-code');

            errEl.style.display = 'none';
            succEl.style.display = 'none';
            btn.disabled = true;
            btn.textContent = 'Sending code…';

            try {
                const response = await fetch(`${API_BASE}/auth/forgot-password`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email })
                });
                const data = await response.json();

                if (response.ok && data.success) {
                    fpEmail = email;
                    document.getElementById('fp-email-display').textContent = email;
                    goToFpStep(2);
                    clearOtpInputs('fp-otp-boxes');
                    startFpResendTimer();
                } else {
                    showError(errEl, data.message || 'Failed to send reset code.');
                }
            } catch (err) {
                showError(errEl, 'Could not reach server. Please try again.');
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<i data-lucide="send"></i> Send Reset Code';
                if (window.lucide) lucide.createIcons();
            }
        });
    }

    // Step 2: Reset Password
    if (step2Form) {
        step2Form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const code = getOtpValue('fp-otp-boxes');
            const newPassword = document.getElementById('fp-new-password').value;
            const confirmPassword = document.getElementById('fp-confirm-password').value;
            const errEl = document.getElementById('fp-error-msg');
            const succEl = document.getElementById('fp-success-msg');
            const btn = document.getElementById('btn-submit-reset-password');

            errEl.style.display = 'none';
            succEl.style.display = 'none';

            if (code.length < 6) {
                return showError(errEl, 'Please enter the 6-digit verification code.');
            }
            if (newPassword.length < 8) {
                return showError(errEl, 'Password must be at least 8 characters.');
            }
            if (newPassword !== confirmPassword) {
                return showError(errEl, 'Passwords do not match.');
            }

            btn.disabled = true;
            btn.textContent = 'Updating…';

            try {
                const response = await fetch(`${API_BASE}/auth/reset-password`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: fpEmail, code, newPassword })
                });
                const data = await response.json();

                if (response.ok && data.success) {
                    succEl.textContent = 'Password updated successfully! You can now sign in.';
                    succEl.style.display = 'block';

                    // Prefill email in login form
                    const usernameInput = document.getElementById('username');
                    if (usernameInput) usernameInput.value = fpEmail;

                    setTimeout(() => {
                        closeForgotPasswordModal();
                        const pwdInput = document.getElementById('password');
                        if (pwdInput) pwdInput.focus();
                    }, 1400);
                } else {
                    showError(errEl, data.message || 'Failed to reset password. Invalid or expired code.');
                }
            } catch (err) {
                showError(errEl, 'Could not reach server. Please try again.');
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<i data-lucide="check"></i> Update Password';
                if (window.lucide) lucide.createIcons();
            }
        });
    }

    // Resend inside Modal
    if (resendBtn) {
        resendBtn.addEventListener('click', async () => {
            if (!fpEmail) return;
            const errEl = document.getElementById('fp-error-msg');
            const succEl = document.getElementById('fp-success-msg');
            errEl.style.display = 'none';
            succEl.style.display = 'none';

            resendBtn.disabled = true;
            try {
                const response = await fetch(`${API_BASE}/auth/forgot-password`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: fpEmail })
                });
                const data = await response.json();
                if (response.ok && data.success) {
                    succEl.textContent = 'A new code has been sent! Check your email inbox.';
                    succEl.style.display = 'block';
                    clearOtpInputs('fp-otp-boxes');
                    startFpResendTimer();
                } else {
                    showError(errEl, data.message || 'Failed to resend code.');
                    resendBtn.disabled = false;
                }
            } catch (err) {
                showError(errEl, 'Could not reach server.');
                resendBtn.disabled = false;
            }
        });
    }
}

function openForgotPasswordModal() {
    const modal = document.getElementById('forgot-password-modal');
    if (!modal) return;

    // Reset forms and errors
    goToFpStep(1);
    const emailInput = document.getElementById('fp-email');
    const loginUser = document.getElementById('username')?.value.trim();
    if (emailInput && loginUser && loginUser.includes('@')) {
        emailInput.value = loginUser;
    }
    document.getElementById('fp-error-msg').style.display = 'none';
    document.getElementById('fp-success-msg').style.display = 'none';

    modal.classList.add('active');
    if (window.lucide) lucide.createIcons();
}

function closeForgotPasswordModal() {
    const modal = document.getElementById('forgot-password-modal');
    if (modal) modal.classList.remove('active');
    if (fpCountdownInterval) clearInterval(fpCountdownInterval);
}

function goToFpStep(step) {
    const step1 = document.getElementById('fp-step-1');
    const step2 = document.getElementById('fp-step-2');
    const dot1  = document.getElementById('fp-dot-1');
    const dot2  = document.getElementById('fp-dot-2');
    const title = document.getElementById('fp-modal-title');
    const sub   = document.getElementById('fp-modal-subtitle');
    const errEl = document.getElementById('fp-error-msg');
    const succEl = document.getElementById('fp-success-msg');

    if (errEl) errEl.style.display = 'none';
    if (succEl) succEl.style.display = 'none';

    if (step === 1) {
        step1.classList.add('active');
        step2.classList.remove('active');
        dot1.classList.add('active');
        dot2.classList.remove('active');
        title.textContent = 'Reset Password';
        sub.textContent = 'We will send a 6-digit verification code to your email';
    } else {
        step1.classList.remove('active');
        step2.classList.add('active');
        dot1.classList.remove('active');
        dot2.classList.add('active');
        title.textContent = 'Enter Verification Code';
        sub.textContent = 'Enter the code from your email and set your new password';
        clearOtpInputs('fp-otp-boxes');
    }
    if (window.lucide) lucide.createIcons();
}

function startFpResendTimer() {
    const resendBtn = document.getElementById('btn-resend-fp-otp');
    const timerContainer = document.getElementById('fp-resend-timer');
    const countdownSpan = document.getElementById('fp-countdown');

    if (!resendBtn || !timerContainer || !countdownSpan) return;

    if (fpCountdownInterval) clearInterval(fpCountdownInterval);

    resendBtn.disabled = true;
    timerContainer.style.display = 'inline';
    let timeLeft = 60;
    countdownSpan.textContent = timeLeft;

    fpCountdownInterval = setInterval(() => {
        timeLeft--;
        countdownSpan.textContent = timeLeft;
        if (timeLeft <= 0) {
            clearInterval(fpCountdownInterval);
            timerContainer.style.display = 'none';
            resendBtn.disabled = false;
        }
    }, 1000);
}

// =============================================================================
// HELPERS & AUTH REDIRECT
// =============================================================================
function showError(el, msg) {
    if (!el) return;
    el.textContent = msg;
    el.style.display = 'block';
}

// Redirect already-logged-in users away from auth pages
window.addEventListener('load', () => {
    const token = localStorage.getItem('token');
    const user  = localStorage.getItem('user');
    if (token && user && (loginForm || registerForm)) {
        try {
            const parsed = JSON.parse(user);
            if (parsed.roles && parsed.roles.includes('ROLE_ADMIN')) {
                window.location.href = 'admin-dashboard.html';
            } else if (parsed.roles && parsed.roles.includes('ROLE_BUS_OWNER')) {
                window.location.href = 'owner-dashboard.html';
            } else {
                window.location.href = 'user-dashboard.html';
            }
        } catch (e) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        }
    }
});
