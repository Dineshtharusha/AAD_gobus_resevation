// ============= Admin Dashboard JS =============

// Check authentication
if (!localStorage.getItem('token')) {
    window.location.href = 'login.html';
}

// Check if user is admin
const user = JSON.parse(localStorage.getItem('user') || '{}');
if (!user.roles || !user.roles.some(r => r.name === 'ROLE_ADMIN')) {
    window.location.href = 'user-dashboard.html';
}

const API_BASE = 'http://localhost:8080/api/v1';

// Initialize Lucide icons
if (window.lucide) lucide.createIcons();

// DOM Elements
const navItems = document.querySelectorAll('.nav-item');
const contentSections = document.querySelectorAll('.content-section');
const pageTitle = document.getElementById('page-title');
const toggleSidebar = document.getElementById('toggle-sidebar');
const adminSidebar = document.querySelector('.admin-sidebar');
const logoutBtn = document.getElementById('logout-btn');
const userMenuToggle = document.querySelector('.user-avatar');

// Setup event listeners
document.addEventListener('DOMContentLoaded', () => {
    setupNavigation();
    setupLogout();
    setupResponsive();
    setupTableActions();
});

// Setup navigation between sections
function setupNavigation() {
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Remove active class from all items
            navItems.forEach(nav => nav.classList.remove('active'));
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
                    'overview': 'Overview',
                    'users': 'User Management',
                    'buses': 'Bus Management',
                    'bookings': 'All Bookings',
                    'routes': 'Route Management',
                    'payments': 'Payment Transactions',
                    'reports': 'Reports',
                    'settings': 'Settings'
                };
                pageTitle.textContent = titles[sectionId] || 'Overview';

                // Close sidebar on mobile
                if (window.innerWidth <= 768) {
                    adminSidebar.classList.remove('active');
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
}

// Setup responsive sidebar
function setupResponsive() {
    if (toggleSidebar) {
        toggleSidebar.addEventListener('click', () => {
            adminSidebar.classList.add('active');
        });
    }

    // Close sidebar on resize
    window.addEventListener('resize', () => {
        if (window.innerWidth > 768) {
            adminSidebar.classList.remove('active');
        }
    });

    // Close sidebar when clicking outside
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.admin-sidebar') && 
            !e.target.closest('.toggle-sidebar') &&
            window.innerWidth <= 768) {
            adminSidebar.classList.remove('active');
        }
    });
}

// Setup table action buttons
function setupTableActions() {
    // Edit buttons
    const editButtons = document.querySelectorAll('[title="Edit"]');
    editButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            alert('Edit feature coming soon!');
        });
    });

    // Delete buttons
    const deleteButtons = document.querySelectorAll('[title="Delete"]');
    deleteButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            if (confirm('Are you sure you want to delete this item?')) {
                alert('Delete feature coming soon!');
            }
        });
    });

    // View buttons
    const viewButtons = document.querySelectorAll('[title="View"]');
    viewButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            alert('View feature coming soon!');
        });
    });

    // Disable buttons
    const disableButtons = document.querySelectorAll('[title="Disable"]');
    disableButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            alert('Disable feature coming soon!');
        });
    });
}

// Setup search functionality
const searchInputs = document.querySelectorAll('.search-input');
searchInputs.forEach(input => {
    input.addEventListener('keyup', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        console.log('Searching for:', searchTerm);
        // Implement search logic
    });
});

// Setup filter selects
const filterSelects = document.querySelectorAll('.filter-select');
filterSelects.forEach(select => {
    select.addEventListener('change', () => {
        const filterValue = select.value;
        console.log('Filter:', filterValue);
        // Implement filter logic
    });
});

// Setup add new buttons
const addButtons = document.querySelectorAll('[id*="add-"]');
addButtons.forEach(btn => {
    btn.addEventListener('click', () => {
        const type = btn.id.replace('add-', '').replace('-btn', '');
        console.log('Add new:', type);
        alert(`Add new ${type} feature coming soon!`);
    });
});

// Setup settings form
const settingsForms = document.querySelectorAll('.settings-form');
settingsForms.forEach(form => {
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        alert('Settings save feature coming soon!');
    });
});

// Fetch admin statistics
async function fetchAdminStats() {
    try {
        const token = localStorage.getItem('token');
        
        // Fetch users count
        const usersResponse = await fetch(`${API_BASE}/users`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        // Fetch bookings count
        const bookingsResponse = await fetch(`${API_BASE}/bookings`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (usersResponse.ok && bookingsResponse.ok) {
            const usersData = await usersResponse.json();
            const bookingsData = await bookingsResponse.json();
            
            console.log('Users:', usersData.data);
            console.log('Bookings:', bookingsData.data);
            
            // Update stats on page
            // This would update the stat cards with real data
        }
    } catch (error) {
        console.error('Error fetching stats:', error);
    }
}

// Export functionality
const exportButtons = document.querySelectorAll('button:contains("Export")');
const allButtons = document.querySelectorAll('button');
allButtons.forEach(btn => {
    if (btn.textContent.includes('Export')) {
        btn.addEventListener('click', () => {
            alert('Export feature coming soon!');
        });
    }
});
