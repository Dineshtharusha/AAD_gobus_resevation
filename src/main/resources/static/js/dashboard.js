// ============= Dashboard JS =============

// Check authentication
if (!localStorage.getItem('token')) {
    window.location.href = 'login.html';
}

const API_BASE = 'http://localhost:8080/api/v1';

// Initialize Lucide icons
if (window.lucide) lucide.createIcons();

// DOM Elements
const sidebarNav = document.querySelectorAll('.nav-item');
const contentSections = document.querySelectorAll('.content-section');
const sectionTitle = document.getElementById('section-title');
const logoutBtn = document.getElementById('logout-btn');
const userMenuToggle = document.getElementById('user-menu-toggle');
const userDropdown = document.getElementById('user-dropdown');
const toggleSidebar = document.getElementById('toggle-sidebar');
const closeSidebar = document.getElementById('close-sidebar');
const sidebar = document.querySelector('.sidebar');

// Setup event listeners
document.addEventListener('DOMContentLoaded', () => {
    loadUserData();
    setupNavigation();
    setupLogout();
    setupUserMenu();
    setupResponsive();
});

// Load user data
function loadUserData() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userName = document.getElementById('user-name');
    const profileName = document.getElementById('profile-name');
    const profileEmail = document.getElementById('profile-email');

    if (userName) userName.textContent = user.fullName || user.username;
    if (profileName) profileName.textContent = user.fullName || user.username;
    if (profileEmail) profileEmail.textContent = user.email;

    // Fetch user bookings
    fetchUserBookings();
}

// Fetch user bookings from API
async function fetchUserBookings() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE}/bookings/my`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            // Update dashboard with real booking data
            console.log('Bookings:', data.data);
        }
    } catch (error) {
        console.error('Error fetching bookings:', error);
    }
}

// Setup navigation between sections
function setupNavigation() {
    sidebarNav.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Remove active class from all items
            sidebarNav.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');

            // Hide all sections
            contentSections.forEach(section => section.classList.remove('active'));

            // Show selected section
            const sectionId = item.getAttribute('data-section');
            const section = document.getElementById(sectionId);
            if (section) {
                section.classList.add('active');
                
                // Update page title
                const titles = {
                    'dashboard': 'Dashboard',
                    'bookings': 'My Bookings',
                    'profile': 'Profile',
                    'notifications': 'Notifications',
                    'reviews': 'My Reviews'
                };
                sectionTitle.textContent = titles[sectionId] || 'Dashboard';

                // Close sidebar on mobile
                if (window.innerWidth <= 768) {
                    sidebar.classList.remove('active');
                }
            }
        });
    });
}

// Setup logout
function setupLogout() {
    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = 'login.html';
    });

    // Also setup dropdown logout
    const logoutDropdown = document.querySelector('#user-dropdown button');
    if (logoutDropdown) {
        logoutDropdown.addEventListener('click', () => {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = 'login.html';
        });
    }
}

// Setup user menu toggle
function setupUserMenu() {
    userMenuToggle.addEventListener('click', () => {
        userDropdown.style.display = userDropdown.style.display === 'none' ? 'block' : 'none';
    });

    // Close dropdown when clicking outside
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.user-menu')) {
            userDropdown.style.display = 'none';
        }
    });
}

// Setup responsive sidebar
function setupResponsive() {
    if (toggleSidebar) {
        toggleSidebar.addEventListener('click', () => {
            sidebar.classList.add('active');
        });
    }

    if (closeSidebar) {
        closeSidebar.addEventListener('click', () => {
            sidebar.classList.remove('active');
        });
    }

    // Close sidebar on resize
    window.addEventListener('resize', () => {
        if (window.innerWidth > 768) {
            sidebar.classList.remove('active');
        }
    });
}

// Setup filter tabs for bookings
const filterTabs = document.querySelectorAll('.filter-tab');
filterTabs.forEach(tab => {
    tab.addEventListener('click', () => {
        filterTabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        
        const filter = tab.getAttribute('data-filter');
        console.log('Filter selected:', filter);
        // Implement filtering logic here
    });
});

// Setup edit profile button
const editProfileBtn = document.getElementById('edit-profile-btn');
if (editProfileBtn) {
    editProfileBtn.addEventListener('click', () => {
        alert('Edit profile feature coming soon!');
    });
}

// Setup change password form
const changePasswordForm = document.getElementById('change-password-form');
if (changePasswordForm) {
    changePasswordForm.addEventListener('submit', (e) => {
        e.preventDefault();
        alert('Password change feature coming soon!');
    });
}

// Setup write review button
const writeReviewBtn = document.querySelector('.section-header .btn-primary');
if (writeReviewBtn) {
    writeReviewBtn.addEventListener('click', () => {
        alert('Review feature coming soon!');
    });
}

// Setup booking action buttons
const bookingActionButtons = document.querySelectorAll('.booking-actions button');
bookingActionButtons.forEach(btn => {
    btn.addEventListener('click', () => {
        const action = btn.textContent.trim();
        console.log('Action:', action);
        // Implement booking actions
    });
});

// Setup quick action buttons
const quickActions = document.querySelectorAll('.quick-action');
quickActions.forEach(btn => {
    btn.addEventListener('click', () => {
        const title = btn.querySelector('h4').textContent;
        console.log('Quick action:', title);
        
        if (title === 'Search & Book') {
            window.location.href = 'index.html';
        }
    });
});
