// =============================================================================
// GoBus AI Assistant — Intelligent Dual-Mode Chatbot Engine
// (Travel & Booking Assistant for passengers / Fleet Assistant for bus operators)
// =============================================================================

(function () {
    'use strict';

    const CHATBOT_API_BASE = (window.location.protocol.startsWith('http') && window.location.port === '8080')
        ? `${window.location.origin}/api/v1`
        : 'http://localhost:8080/api/v1';

    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');

    // Detect mode: Owner dashboard vs Passenger/Visitor page
    const isOwnerPage = window.location.pathname.includes('owner-dashboard') ||
        document.title.toLowerCase().includes('bus operator') ||
        document.title.toLowerCase().includes('fleet');

    // ---- Cached Data ----
    let cachedBuses = null;
    let cachedSchedules = null;
    let cachedBookings = null;
    let cachedRoutes = null;
    let dataLoaded = false;

    // ---- DOM References ----
    let messagesContainer, inputField, sendBtn, chipContainer;

    // ---- Initialize ----
    function initChatbot() {
        const fab = document.getElementById('chatbot-fab');
        const panel = document.getElementById('chatbot-panel');
        const closeBtn = document.getElementById('chatbot-close');
        inputField = document.getElementById('chatbot-input');
        sendBtn = document.getElementById('chatbot-send');
        messagesContainer = document.getElementById('chatbot-messages');
        chipContainer = document.getElementById('chatbot-chips');

        if (!fab || !panel) return;

        // Customize header title based on page mode
        const titleEl = document.getElementById('chatbot-title') || panel.querySelector('.chatbot-header-info h4');
        if (titleEl) {
            titleEl.textContent = isOwnerPage ? 'GoBus Fleet Assistant' : 'GoBus Travel Assistant';
        }

        // Toggle panel
        fab.addEventListener('click', () => {
            const isOpen = panel.classList.contains('open');
            if (isOpen) {
                panel.classList.remove('open');
                fab.classList.remove('active');
            } else {
                panel.classList.add('open');
                fab.classList.add('active');
                fab.querySelector('.notif-dot')?.remove();
                if (!dataLoaded) {
                    loadAllData();
                }
                inputField?.focus();
            }
        });

        closeBtn?.addEventListener('click', () => {
            panel.classList.remove('open');
            fab.classList.remove('active');
        });

        // Send on Enter
        inputField?.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                handleSend();
            }
        });

        sendBtn?.addEventListener('click', handleSend);

        // Quick chips
        chipContainer?.addEventListener('click', (e) => {
            const chip = e.target.closest('.chip-btn');
            if (chip) {
                const query = chip.getAttribute('data-query');
                if (inputField) inputField.value = query;
                handleSend();
            }
        });

        // Initial welcome message
        if (isOwnerPage) {
            addBotMessage(
                `👋 Hello! I'm your <strong>GoBus Fleet Assistant</strong>.<br><br>` +
                `I can help you with real-time information about your fleet, passengers, schedules, and revenue. Try asking me something!`
            );
            showChips(defaultOwnerChips());
        } else {
            addBotMessage(
                `👋 Ayubowan! I'm your <strong>GoBus Travel Assistant</strong>. 🚌<br><br>` +
                `Planning a journey across Sri Lanka? I can help you with routes, ticket fares, live bus schedules, seat selection, and cancellation policies!`
            );
            showChips(defaultTravelerChips());
        }
    }

    // ---- Data Loading ----
    async function loadAllData() {
        try {
            if (isOwnerPage) {
                const headers = token ? { 'Authorization': `Bearer ${token}` } : {};
                const [busRes, schRes, bookRes] = await Promise.all([
                    fetch(`${CHATBOT_API_BASE}/buses/my`, { headers }).then(r => r.json()).catch(() => null),
                    fetch(`${CHATBOT_API_BASE}/schedules/owner`, { headers }).then(r => r.json()).catch(() => null),
                    fetch(`${CHATBOT_API_BASE}/bookings/owner/passengers`, { headers }).then(r => r.json()).catch(() => null)
                ]);

                cachedBuses = busRes?.success ? (busRes.data || []) : [];
                cachedSchedules = schRes?.success ? (schRes.data || []) : [];
                cachedBookings = bookRes?.success ? (bookRes.data || []) : [];
            } else {
                // Public passenger mode: load public routes and schedules
                const [routesRes, schRes] = await Promise.all([
                    fetch(`${CHATBOT_API_BASE}/routes`).then(r => r.json()).catch(() => null),
                    fetch(`${CHATBOT_API_BASE}/schedules`).then(r => r.json()).catch(() => null)
                ]);

                cachedRoutes = routesRes?.success ? (routesRes.data || []) : [];
                cachedSchedules = schRes?.success ? (schRes.data || []) : [];
            }
            dataLoaded = true;
        } catch (e) {
            console.error('Chatbot data load error:', e);
            cachedBuses = [];
            cachedSchedules = [];
            cachedBookings = [];
            cachedRoutes = [];
        }
    }

    async function refreshData() {
        dataLoaded = false;
        await loadAllData();
    }

    // ---- Message Handling ----
    function handleSend() {
        if (!inputField) return;
        const text = inputField.value.trim();
        if (!text) return;

        addUserMessage(text);
        inputField.value = '';
        if (chipContainer) chipContainer.innerHTML = '';

        // Show typing indicator
        const typingEl = showTyping();

        setTimeout(async () => {
            if (!dataLoaded) await loadAllData();
            const response = isOwnerPage ? processOwnerQuery(text) : processTravelerQuery(text);
            typingEl.remove();
            addBotMessage(response.message);
            if (response.chips) showChips(response.chips);
        }, 500 + Math.random() * 300);
    }

    function addUserMessage(text) {
        let initials = 'U';
        try {
            const u = JSON.parse(userStr);
            initials = u.username ? u.username.substring(0, 2).toUpperCase() : 'U';
        } catch (e) { }

        const html = `
            <div class="chat-msg user">
                <div class="msg-avatar">${initials}</div>
                <div class="msg-bubble">
                    ${escapeHtml(text)}
                    <span class="msg-time">${formatTime()}</span>
                </div>
            </div>`;
        messagesContainer.insertAdjacentHTML('beforeend', html);
        scrollToBottom();
    }

    function addBotMessage(html) {
        const msg = `
            <div class="chat-msg bot">
                <div class="msg-avatar">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;">
                        <path d="M12 8V4H8"/><rect width="16" height="12" x="4" y="8" rx="2"/><path d="M2 14h2"/><path d="M20 14h2"/><path d="M15 13v2"/><path d="M9 13v2"/>
                    </svg>
                </div>
                <div class="msg-bubble">
                    ${html}
                    <span class="msg-time">${formatTime()}</span>
                </div>
            </div>`;
        messagesContainer.insertAdjacentHTML('beforeend', msg);
        scrollToBottom();
    }

    function showTyping() {
        const html = `<div class="typing-indicator" id="typing-ind"><span></span><span></span><span></span></div>`;
        messagesContainer.insertAdjacentHTML('beforeend', html);
        scrollToBottom();
        return document.getElementById('typing-ind');
    }

    function showChips(chips) {
        if (!chipContainer) return;
        chipContainer.innerHTML = chips.map(c =>
            `<button class="chip-btn" data-query="${escapeHtml(c.query)}">${c.label}</button>`
        ).join('');
    }

    // =========================================================================
    // TRAVELER / VISITOR QUERY PROCESSOR (index.html, user-dashboard.html)
    // =========================================================================
    function processTravelerQuery(input) {
        const q = input.toLowerCase().trim();
        const routes = cachedRoutes || [];
        const schedules = cachedSchedules || [];

        // Greetings
        if (matchAny(q, ['hello', 'hi', 'hey', 'good morning', 'good afternoon', 'good evening', 'ayubowan', 'vanakkam'])) {
            return {
                message: `Ayubowan! 😊 Where would you like to travel today? Ask me about our popular routes, bus departures, fares, or how to reserve your seats!`,
                chips: defaultTravelerChips()
            };
        }

        // Help
        if (matchAny(q, ['help', 'what can you do', 'menu', 'options', 'features'])) {
            return {
                message:
                    `I can assist you with any of the following:<br><br>` +
                    `🗺️ <strong>Routes & Fares</strong> — "Show routes", "Ticket price to Kandy"<br>` +
                    `📅 <strong>Schedules</strong> — "Today's buses", "Next departure to Galle"<br>` +
                    `🎫 <strong>Booking Guide</strong> — "How to book a ticket", "Seat selection"<br>` +
                    `💳 <strong>Payments</strong> — "Payment methods accepted"<br>` +
                    `❌ <strong>Cancellation</strong> — "Cancellation and refund policy"<br>` +
                    `🧳 <strong>Amenities</strong> — "Luggage allowance", "Wi-Fi & AC"<br>` +
                    `📞 <strong>Support</strong> — "Customer care hotline", "Contact us"`,
                chips: defaultTravelerChips()
            };
        }

        // Routes & Fares
        if (matchAny(q, ['route', 'routes', 'destinations', 'where do you go', 'cities', 'places', 'fare', 'fares', 'price', 'ticket price', 'how much', 'cost'])) {
            if (routes.length > 0) {
                let routeCards = routes.slice(0, 6).map(r =>
                    `<div class="chat-data-card">
                        <div class="data-row"><span class="data-label">Route</span><span class="data-value highlight"><strong>${r.source} &rarr; ${r.destination}</strong></span></div>
                        <div class="data-row"><span class="data-label">Distance &amp; Time</span><span class="data-value">${r.distanceKm} km (${formatDuration(r.estimatedDurationMinutes)})</span></div>
                        <div class="data-row"><span class="data-label">Base Fare</span><span class="data-value" style="color:#059669;font-weight:700;">Rs. ${Number(r.basePrice).toLocaleString()}</span></div>
                    </div>`
                ).join('');

                return {
                    message: `🗺️ GoBus operates key intercity express routes across Sri Lanka:<br>${routeCards}`,
                    chips: [
                        { label: '📅 Today\'s Schedules', query: 'show schedules' },
                        { label: '🎫 How to Book', query: 'how to book' },
                        { label: '📞 Contact Support', query: 'contact' }
                    ]
                };
            }

            return {
                message:
                    `🗺️ <strong>Popular GoBus Routes &amp; Fares:</strong><br><br>` +
                    `• <strong>Colombo &harr; Kandy:</strong> From Rs. 1,550 (4h 15m)<br>` +
                    `• <strong>Colombo &harr; Galle:</strong> From Rs. 950 (2h Expressway)<br>` +
                    `• <strong>Colombo &harr; Jaffna:</strong> From Rs. 2,450 (8h Overnight Luxury)<br>` +
                    `• <strong>Kandy &harr; Badulla / Ella:</strong> From Rs. 1,200 (2h 30m scenic)<br>` +
                    `• <strong>Colombo &harr; Matara:</strong> From Rs. 1,100 (3h Southern Expressway)<br><br>` +
                    `Use the search box above to choose your exact travel date and seat!`,
                chips: [
                    { label: '📅 Schedules', query: 'show schedules' },
                    { label: '🎫 How to Book', query: 'how to book' }
                ]
            };
        }

        // Schedules & Timings
        if (matchAny(q, ['schedule', 'schedules', 'timetable', 'times', 'departure', 'today', 'tomorrow', 'trips', 'buses available'])) {
            if (schedules.length > 0) {
                const upcoming = schedules
                    .filter(s => new Date(s.departureTime) > new Date())
                    .slice(0, 5);

                const displayList = upcoming.length > 0 ? upcoming : schedules.slice(0, 5);

                let schedCards = displayList.map(s => {
                    const dep = s.departureTime ? new Date(s.departureTime).toLocaleString('en-US', {
                        month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
                    }) : '-';
                    return `<div class="chat-data-card">
                        <div class="data-row"><span class="data-label">Trip</span><span class="data-value highlight"><strong>${s.source} &rarr; ${s.destination}</strong></span></div>
                        <div class="data-row"><span class="data-label">Coach</span><span class="data-value">${s.busName || 'GoBus Express'}</span></div>
                        <div class="data-row"><span class="data-label">Departure</span><span class="data-value">${dep}</span></div>
                        <div class="data-row"><span class="data-label">Seats Left</span><span class="data-value" style="color:#2563eb;font-weight:700;">${s.availableSeats} available</span></div>
                    </div>`;
                }).join('');

                return {
                    message: `📅 <strong>Available Bus Departures:</strong><br>${schedCards}`,
                    chips: [
                        { label: '🎫 How to Book', query: 'how to book' },
                        { label: '🗺️ Routes & Fares', query: 'routes' }
                    ]
                };
            }

            return {
                message:
                    `📅 <strong>Daily GoBus Departures:</strong><br><br>` +
                    `• <strong>Colombo &rarr; Kandy:</strong> 06:30, 08:00, 10:30, 14:00, 17:30<br>` +
                    `• <strong>Colombo &rarr; Galle:</strong> 06:00, 09:00, 13:00, 16:30, 19:00<br>` +
                    `• <strong>Colombo &rarr; Jaffna:</strong> 20:00, 21:30 (Luxury Sleepers)<br>` +
                    `• <strong>Kandy &rarr; Badulla / Ella:</strong> 07:30, 11:00, 15:00<br><br>` +
                    `Select your origin and destination in the search card above for real-time seat availability.`,
                chips: [
                    { label: '🎫 How to Book', query: 'how to book' },
                    { label: '🗺️ Routes & Fares', query: 'routes' }
                ]
            };
        }

        // How to Book
        if (matchAny(q, ['how to book', 'book ticket', 'reserve seat', 'buy ticket', 'step', 'how does it work', 'booking process', 'reserve'])) {
            return {
                message:
                    `🎫 <strong>Booking on GoBus takes just 4 easy steps:</strong><br><br>` +
                    `1️⃣ <strong>Search:</strong> Select your departure city, destination, and travel date in the search card above.<br>` +
                    `2️⃣ <strong>Choose:</strong> Compare departure times, luxury amenities, and bus operators.<br>` +
                    `3️⃣ <strong>Select Seats:</strong> Click "Choose seats" to open our interactive bus layout and pick your favourite window or aisle seat.<br>` +
                    `4️⃣ <strong>Instant E-Ticket:</strong> Complete payment securely. Your boarding pass with QR verification is issued immediately!`,
                chips: [
                    { label: '💳 Payment Methods', query: 'payment' },
                    { label: '❌ Cancellation Policy', query: 'cancellation' },
                    { label: '🗺️ Routes & Fares', query: 'routes' }
                ]
            };
        }

        // Cancellation & Refunds
        if (matchAny(q, ['cancel', 'cancellation', 'refund', 'money back', 'change date', 'reschedule'])) {
            return {
                message:
                    `❌ <strong>GoBus Flexible Cancellation Policy:</strong><br><br>` +
                    `• <strong>&gt; 24 Hours before departure:</strong> <strong>90% instant refund</strong>.<br>` +
                    `• <strong>12 to 24 Hours before departure:</strong> <strong>50% refund</strong>.<br>` +
                    `• <strong>Under 12 Hours or No-Show:</strong> Non-refundable.<br><br>` +
                    `💡 You can cancel tickets with a single click from your <strong>My Bookings</strong> section when signed in, or call our 24/7 hotline. Refunds are processed back to your card within 2–3 banking days.`,
                chips: [
                    { label: '📞 Contact Support', query: 'contact' },
                    { label: '🎫 How to Book', query: 'how to book' }
                ]
            };
        }

        // Luggage & Amenities
        if (matchAny(q, ['luggage', 'baggage', 'bag', 'amenities', 'wifi', 'wi-fi', 'ac', 'charger', 'usb', 'water', 'facilities'])) {
            return {
                message:
                    `🧳 <strong>Baggage Allowance &amp; Bus Amenities:</strong><br><br>` +
                    `• <strong>Free Baggage:</strong> 20 kg check-in luggage + 7 kg cabin bag per passenger.<br>` +
                    `• <strong>Climate Control:</strong> Full luxury AC on all express coaches.<br>` +
                    `• <strong>Free High-Speed Wi-Fi:</strong> Keep connected during your journey.<br>` +
                    `• <strong>USB Charging:</strong> Individual charging ports beside every seat.<br>` +
                    `• <strong>Comfort:</strong> High-grade push-back reclining seats with leg rests.<br>` +
                    `• <strong>Water:</strong> Complimentary mineral water bottle provided on long-distance trips.`,
                chips: [
                    { label: '🎫 How to Book', query: 'how to book' },
                    { label: '🗺️ Routes & Fares', query: 'routes' }
                ]
            };
        }

        // Payment Methods
        if (matchAny(q, ['payment', 'pay', 'card', 'visa', 'mastercard', 'qr', 'lankaqr', 'cash', 'checkout'])) {
            return {
                message:
                    `💳 <strong>Accepted Payment Methods:</strong><br><br>` +
                    `• <strong>Credit &amp; Debit Cards:</strong> Visa, MasterCard, UnionPay.<br>` +
                    `• <strong>Mobile &amp; Wallets:</strong> LankaQR, FriMi, Genie, eZ Cash.<br>` +
                    `• <strong>Security:</strong> All transactions are protected with 256-bit SSL encryption and 3D-Secure bank OTP.`,
                chips: [
                    { label: '🎫 How to Book', query: 'how to book' },
                    { label: '❌ Cancellation Policy', query: 'cancellation' }
                ]
            };
        }

        // Contact & Support
        if (matchAny(q, ['contact', 'support', 'helpdesk', 'phone', 'hotline', 'call', 'number', 'email', 'office', 'whatsapp', 'address'])) {
            return {
                message:
                    `📞 <strong>GoBus 24/7 Passenger Support:</strong><br><br>` +
                    `• <strong>Toll-Free Hotline:</strong> <a href="tel:+94112345678" style="color:#2563eb;font-weight:700;">+94 11 234 5678</a><br>` +
                    `• <strong>Mobile &amp; WhatsApp:</strong> <a href="tel:+94771234567" style="color:#2563eb;font-weight:700;">+94 77 123 4567</a><br>` +
                    `• <strong>Support Email:</strong> <a href="mailto:support@gobus.lk" style="color:#2563eb;font-weight:700;">support@gobus.lk</a><br>` +
                    `• <strong>Headquarters:</strong> No. 45, Galle Road, Colombo 03<br>` +
                    `• <strong>Main Terminal Counters:</strong> Colombo Bastian Mawatha, Kandy Goods Shed, Galle Central, Jaffna Central.`,
                chips: [
                    { label: '🗺️ Routes & Fares', query: 'routes' },
                    { label: '🎫 How to Book', query: 'how to book' }
                ]
            };
        }

        // Offers & Discounts
        if (matchAny(q, ['offer', 'discount', 'promo', 'coupon', 'deal', 'code', 'save', 'cheap'])) {
            return {
                message:
                    `🏷️ <strong>Current GoBus Special Offers:</strong><br><br>` +
                    `• <strong>ELLA20:</strong> Get 20% off on all weekend getaways to Ella & Badulla!<br>` +
                    `• <strong>ROUNDTRIP10:</strong> Save 10% when you book return journeys together.<br>` +
                    `• <strong>Student Travel:</strong> 15% discount with valid university ID card at terminal counters.`,
                chips: [
                    { label: '🗺️ Routes & Fares', query: 'routes' },
                    { label: '🎫 How to Book', query: 'how to book' }
                ]
            };
        }

        // E-Ticket & Boarding
        if (matchAny(q, ['ticket', 'e-ticket', 'boarding', 'qr', 'print', 'show ticket'])) {
            return {
                message:
                    `📱 <strong>Your Digital E-Ticket:</strong><br><br>` +
                    `• After payment, your e-ticket with a unique QR code and barcode is generated instantly.<br>` +
                    `• You will receive a copy via email, and you can download it anytime from the <strong>My Bookings</strong> dashboard.<br>` +
                    `• Simply show the QR code on your phone screen (or printed copy) along with your National Identity Card (NIC) to the conductor at boarding.`,
                chips: [
                    { label: '🎫 How to Book', query: 'how to book' },
                    { label: '📞 Contact Support', query: 'contact' }
                ]
            };
        }

        // Account & Registration
        if (matchAny(q, ['login', 'sign in', 'register', 'sign up', 'create account', 'account', 'password'])) {
            return {
                message:
                    `👤 <strong>GoBus Account Benefits:</strong><br><br>` +
                    `Creating an account is 100% free and gives you access to:<br>` +
                    `• Instant access to all your e-tickets and travel invoices.<br>` +
                    `• One-click cancellation and refund requests.<br>` +
                    `• Real-time trip status and departure delay alerts.<br><br>` +
                    `<a href="login.html" style="color:#2563eb;font-weight:700;">Sign in here</a> or <a href="register.html" style="color:#2563eb;font-weight:700;">Create a free account</a>!`,
                chips: [
                    { label: '🎫 How to Book', query: 'how to book' },
                    { label: '📞 Contact Support', query: 'contact' }
                ]
            };
        }

        // Bus Owner questions on public page
        if (matchAny(q, ['bus owner', 'operator', 'my bus', 'my fleet', 'add bus', 'driver', 'owner'])) {
            return {
                message:
                    `🚌 <strong>Are you a Bus Operator / Fleet Owner?</strong><br><br>` +
                    `GoBus provides a dedicated Fleet Operator Dashboard where you can monitor your buses, seat occupancy, and daily revenue in real time.<br><br>` +
                    `Please <a href="login.html" style="color:#2563eb;font-weight:700;">sign in with your Bus Owner account</a> to access your operator dashboard!`,
                chips: [
                    { label: '📞 Contact Support', query: 'contact' },
                    { label: '🗺️ Routes & Fares', query: 'routes' }
                ]
            };
        }

        // Thanks
        if (matchAny(q, ['thank', 'thanks', 'thank you', 'great', 'awesome', 'good job'])) {
            return {
                message: `You're very welcome! 😊 Have a wonderful journey with GoBus. Feel free to ask if you have any more questions!`,
                chips: defaultTravelerChips()
            };
        }

        // Goodbye
        if (matchAny(q, ['bye', 'goodbye', 'see you', 'exit', 'close'])) {
            return {
                message: `Goodbye! Safe travels with GoBus. Have an amazing trip across Sri Lanka! 🚌🌴`,
                chips: []
            };
        }

        // Fallback for traveler
        return {
            message:
                `I'm here to help you travel smoother! 🤔 You can ask me about:<br><br>` +
                `• <strong>Routes &amp; Fares:</strong> "What are the ticket prices?"<br>` +
                `• <strong>Schedules:</strong> "When is the next bus to Kandy?"<br>` +
                `• <strong>How to Book:</strong> "How do I reserve seats?"<br>` +
                `• <strong>Policies:</strong> "Cancellation rules" or "Luggage allowance"<br>` +
                `• <strong>Support:</strong> "Customer hotline"`,
            chips: defaultTravelerChips()
        };
    }

    // =========================================================================
    // OWNER QUERY PROCESSOR (owner-dashboard.html)
    // =========================================================================
    function processOwnerQuery(input) {
        const q = input.toLowerCase().trim();
        const buses = cachedBuses || [];
        const schedules = cachedSchedules || [];
        const bookings = cachedBookings || [];

        // Greetings
        if (matchAny(q, ['hello', 'hi', 'hey', 'good morning', 'good afternoon', 'good evening', 'howdy'])) {
            return {
                message: `Hello! 😊 How can I help you with your fleet today?`,
                chips: defaultOwnerChips()
            };
        }

        // Help
        if (matchAny(q, ['help', 'what can you do', 'commands', 'features', 'menu', 'options'])) {
            return {
                message:
                    `I can help you with these fleet topics:<br><br>` +
                    `🚌 <strong>Fleet</strong> — "Show my buses", "How many buses?"<br>` +
                    `👥 <strong>Passengers</strong> — "How many passengers?", "Passenger list"<br>` +
                    `💰 <strong>Revenue</strong> — "Total revenue", "Revenue breakdown"<br>` +
                    `📅 <strong>Schedules</strong> — "Upcoming schedules", "Next departure"<br>` +
                    `📊 <strong>Analytics</strong> — "Most booked bus", "Booking stats"<br>` +
                    `🔄 <strong>Refresh</strong> — "Refresh data"`,
                chips: defaultOwnerChips()
            };
        }

        // Refresh
        if (matchAny(q, ['refresh', 'reload', 'update data', 'sync'])) {
            refreshData();
            return {
                message: `🔄 Data refreshed! I've loaded the latest information from your fleet. Ask me anything!`,
                chips: defaultOwnerChips()
            };
        }

        // Fleet / Bus Queries
        if (matchAny(q, ['my buses', 'my fleet', 'show buses', 'list buses', 'how many buses', 'fleet status', 'bus count', 'buses do i have'])) {
            if (buses.length === 0) {
                return {
                    message: `You don't have any buses assigned to your account yet. Contact your administrator to get buses assigned.`,
                    chips: [{ label: '❓ Help', query: 'help' }]
                };
            }

            let busCards = buses.map(b =>
                `<div class="chat-data-card">
                    <div class="data-row"><span class="data-label">Bus Name</span><span class="data-value">${b.busName}</span></div>
                    <div class="data-row"><span class="data-label">Number</span><span class="data-value highlight">${b.busNumber}</span></div>
                    <div class="data-row"><span class="data-label">Type</span><span class="data-value">${b.busType}</span></div>
                    <div class="data-row"><span class="data-label">Seats</span><span class="data-value">${b.totalSeats}</span></div>
                    <div class="data-row"><span class="data-label">Status</span><span class="data-value">${b.active ? '🟢 Active' : '🔴 Inactive'}</span></div>
                </div>`
            ).join('');

            const totalSeats = buses.reduce((s, b) => s + (b.totalSeats || 0), 0);
            return {
                message: `🚌 You have <strong>${buses.length} bus${buses.length > 1 ? 'es' : ''}</strong> in your fleet with a total of <strong>${totalSeats} seats</strong>.${busCards}`,
                chips: [
                    { label: '📅 Schedules', query: 'upcoming schedules' },
                    { label: '👥 Passengers', query: 'how many passengers' },
                    { label: '💰 Revenue', query: 'total revenue' }
                ]
            };
        }

        // Passenger Queries
        if (matchAny(q, ['passengers', 'passenger count', 'how many passengers', 'passenger list', 'traveler', 'booked people', 'manifest'])) {
            if (bookings.length === 0) {
                return {
                    message: `No passenger bookings found for your fleet at the moment.`,
                    chips: [{ label: '🚌 My Fleet', query: 'show my buses' }, { label: '📅 Schedules', query: 'upcoming schedules' }]
                };
            }

            const confirmed = bookings.filter(b => b.status === 'CONFIRMED').length;
            const pending = bookings.filter(b => b.status === 'PENDING').length;
            const cancelled = bookings.filter(b => b.status === 'CANCELLED').length;
            const withPhone = bookings.filter(b => b.passengerPhone).length;

            return {
                message:
                    `👥 You have <strong>${bookings.length} total passenger booking${bookings.length > 1 ? 's' : ''}</strong> across your fleet.` +
                    `<div class="chat-data-card">
                        <div class="data-row"><span class="data-label">✅ Confirmed</span><span class="data-value highlight">${confirmed}</span></div>
                        <div class="data-row"><span class="data-label">⏳ Pending</span><span class="data-value">${pending}</span></div>
                        <div class="data-row"><span class="data-label">❌ Cancelled</span><span class="data-value">${cancelled}</span></div>
                        <div class="data-row"><span class="data-label">📞 With Phone</span><span class="data-value">${withPhone}</span></div>
                    </div>`,
                chips: [
                    { label: '💰 Revenue', query: 'total revenue' },
                    { label: '📊 Top Bus', query: 'most booked bus' },
                    { label: '🚌 My Fleet', query: 'show my buses' }
                ]
            };
        }

        // Revenue Queries
        if (matchAny(q, ['revenue', 'earnings', 'income', 'money', 'total revenue', 'how much earned', 'sales'])) {
            if (bookings.length === 0) {
                return {
                    message: `No revenue data available yet. Revenue is calculated from confirmed passenger bookings.`,
                    chips: defaultOwnerChips()
                };
            }

            const totalRev = bookings.reduce((s, b) => s + (Number(b.totalAmount) || 0), 0);
            const confirmedRev = bookings.filter(b => b.status === 'CONFIRMED').reduce((s, b) => s + (Number(b.totalAmount) || 0), 0);

            const busTotals = {};
            bookings.forEach(b => {
                const key = b.busName || b.busNumber || 'Unknown';
                busTotals[key] = (busTotals[key] || 0) + (Number(b.totalAmount) || 0);
            });

            let breakdownHtml = Object.entries(busTotals)
                .sort((a, b) => b[1] - a[1])
                .map(([name, amt]) =>
                    `<div class="data-row"><span class="data-label">${name}</span><span class="data-value">LKR ${amt.toLocaleString()}</span></div>`
                ).join('');

            return {
                message:
                    `💰 Fleet Revenue Summary:` +
                    `<div class="chat-data-card">
                        <div class="data-row"><span class="data-label">Total Revenue</span><span class="data-value highlight">LKR ${totalRev.toLocaleString()}</span></div>
                        <div class="data-row"><span class="data-label">Confirmed Revenue</span><span class="data-value">LKR ${confirmedRev.toLocaleString()}</span></div>
                        <div class="data-row"><span class="data-label">Total Bookings</span><span class="data-value">${bookings.length}</span></div>
                    </div>` +
                    (breakdownHtml ? `<br>📊 <strong>By Bus:</strong><div class="chat-data-card">${breakdownHtml}</div>` : ''),
                chips: [
                    { label: '👥 Passengers', query: 'how many passengers' },
                    { label: '📊 Top Bus', query: 'most booked bus' },
                    { label: '📅 Schedules', query: 'upcoming schedules' }
                ]
            };
        }

        // Schedules Queries
        if (matchAny(q, ['schedule', 'schedules', 'trips', 'departure', 'upcoming', 'next departure', 'trip schedule', 'timetable'])) {
            if (schedules.length === 0) {
                return {
                    message: `No active schedules found for your fleet. Schedules are created by administrators for your buses.`,
                    chips: [{ label: '🚌 My Fleet', query: 'show my buses' }, { label: '❓ Help', query: 'help' }]
                };
            }

            const upcoming = schedules
                .filter(s => new Date(s.departureTime) > new Date())
                .sort((a, b) => new Date(a.departureTime) - new Date(b.departureTime))
                .slice(0, 5);

            const displayList = upcoming.length > 0 ? upcoming : schedules.slice(0, 5);

            let schedCards = displayList.map(s => {
                const dep = s.departureTime ? new Date(s.departureTime).toLocaleString('en-US', {
                    month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
                }) : '-';
                return `<div class="chat-data-card">
                    <div class="data-row"><span class="data-label">Route</span><span class="data-value highlight">${s.source} &rarr; ${s.destination}</span></div>
                    <div class="data-row"><span class="data-label">Bus</span><span class="data-value">${s.busName} (${s.busNumber})</span></div>
                    <div class="data-row"><span class="data-label">Departure</span><span class="data-value">${dep}</span></div>
                    <div class="data-row"><span class="data-label">Available Seats</span><span class="data-value">${s.availableSeats}</span></div>
                </div>`;
            }).join('');

            return {
                message: `📅 <strong>Schedules for Your Fleet:</strong>${schedCards}`,
                chips: [
                    { label: '👥 Passengers', query: 'how many passengers' },
                    { label: '💰 Revenue', query: 'total revenue' }
                ]
            };
        }

        // Overview / Summary
        if (matchAny(q, ['summary', 'overview', 'dashboard', 'status', 'report'])) {
            const totalBuses = buses.length;
            const totalPax = bookings.length;
            const totalRev = bookings.reduce((s, b) => s + (Number(b.totalAmount) || 0), 0);
            const totalSchedules = schedules.length;
            const confirmedPax = bookings.filter(b => b.status === 'CONFIRMED').length;

            return {
                message:
                    `📋 <strong>Fleet Overview:</strong>` +
                    `<div class="chat-data-card">
                        <div class="data-row"><span class="data-label">🚌 Total Buses</span><span class="data-value highlight">${totalBuses}</span></div>
                        <div class="data-row"><span class="data-label">📅 Active Schedules</span><span class="data-value">${totalSchedules}</span></div>
                        <div class="data-row"><span class="data-label">👥 Total Passengers</span><span class="data-value">${totalPax}</span></div>
                        <div class="data-row"><span class="data-label">✅ Confirmed</span><span class="data-value">${confirmedPax}</span></div>
                        <div class="data-row"><span class="data-label">💰 Total Revenue</span><span class="data-value highlight">LKR ${totalRev.toLocaleString()}</span></div>
                    </div>`,
                chips: [
                    { label: '🚌 Fleet Details', query: 'show my buses' },
                    { label: '📅 Schedules', query: 'upcoming schedules' }
                ]
            };
        }

        // Thanks
        if (matchAny(q, ['thank', 'thanks', 'thank you', 'appreciate', 'great'])) {
            return {
                message: `You're welcome! 😊 Let me know if you need anything else about your fleet.`,
                chips: defaultOwnerChips()
            };
        }

        // Goodbye
        if (matchAny(q, ['bye', 'goodbye', 'see you', 'later', 'exit', 'close'])) {
            return {
                message: `Goodbye! 👋 Have a great day managing your fleet. I'm always here if you need me!`,
                chips: []
            };
        }

        // Fallback for owner
        return {
            message:
                `I'm not sure I understood that. 🤔 Try asking about:<br>` +
                `• <strong>Fleet</strong> — "Show my buses"<br>` +
                `• <strong>Passengers</strong> — "How many passengers?"<br>` +
                `• <strong>Revenue</strong> — "Total revenue"<br>` +
                `• <strong>Schedules</strong> — "Upcoming schedules"<br>` +
                `• <strong>Overview</strong> — "Summary"<br><br>` +
                `Or type <strong>"help"</strong> for all commands.`,
            chips: defaultOwnerChips()
        };
    }

    // ---- Helpers ----
    function matchAny(input, keywords) {
        return keywords.some(kw => input.includes(kw));
    }

    function defaultTravelerChips() {
        return [
            { label: '🗺️ Routes & Fares', query: 'routes and fares' },
            { label: '📅 Today\'s Schedules', query: 'schedules today' },
            { label: '🎫 How to Book', query: 'how to book a ticket' },
            { label: '📞 Contact Support', query: 'contact support' },
            { label: '❌ Cancellation Policy', query: 'cancellation policy' },
            { label: '🧳 Amenities', query: 'luggage and amenities' }
        ];
    }

    function defaultOwnerChips() {
        return [
            { label: '🚌 My Fleet', query: 'show my buses' },
            { label: '👥 Passengers', query: 'how many passengers' },
            { label: '💰 Revenue', query: 'total revenue' },
            { label: '📅 Schedules', query: 'upcoming schedules' },
            { label: '📋 Summary', query: 'summary' }
        ];
    }

    function formatDuration(mins) {
        if (!mins) return 'N/A';
        const h = Math.floor(mins / 60);
        const m = mins % 60;
        return h > 0 ? `${h}h ${m > 0 ? m + 'm' : ''}` : `${m}m`;
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function formatTime() {
        return new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
    }

    function scrollToBottom() {
        if (messagesContainer) {
            requestAnimationFrame(() => {
                messagesContainer.scrollTop = messagesContainer.scrollHeight;
            });
        }
    }

    // ---- Boot ----
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initChatbot);
    } else {
        initChatbot();
    }

})();
