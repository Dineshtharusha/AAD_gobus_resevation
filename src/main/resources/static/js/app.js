window.API_BASE = window.API_BASE || ((window.location.protocol.startsWith('http') && window.location.port === '8080')
    ? `${window.location.origin}/api/v1`
    : 'http://localhost:8080/api/v1');
var API_BASE = window.API_BASE;

// -- Date field init ----------------------------------------------------------
const dateInput = document.querySelector('#travel-date');
const today = new Date();
dateInput.min = today.toISOString().split('T')[0];
dateInput.value = today.toISOString().split('T')[0];

if (window.lucide) lucide.createIcons();

// -- City swap ----------------------------------------------------------------
const fromCity = document.querySelector('#from-city');
const toCity   = document.querySelector('#to-city');

document.querySelector('#swap-cities').addEventListener('click', () => {
    const tmp  = fromCity.value;
    fromCity.value = toCity.value;
    toCity.value   = tmp;
});

// -- Trip type tabs -----------------------------------------------------------
document.querySelectorAll('.trip-tab').forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelectorAll('.trip-tab').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
    });
});

function formatDate(value) {
    return new Intl.DateTimeFormat('en-LK', {
        day: 'numeric', month: 'short', year: 'numeric'
    }).format(new Date(`${value}T00:00:00`));
}

// -- Popular route cards click ------------------------------------------------
document.querySelectorAll('.route-card').forEach(route => {
    route.addEventListener('click', () => {
        fromCity.value = route.dataset.from;
        toCity.value   = route.dataset.to;
        document.querySelector('#search-card').scrollIntoView({ behavior: 'smooth', block: 'center' });
    });
});

// -- SEARCH FORM -- calls real API ----------------------------------------------
const searchForm     = document.querySelector('#search-form');
const resultsSection = document.querySelector('#results');
const journeyList    = document.querySelector('.journey-list');

searchForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const source      = fromCity.value.trim();
    const destination = toCity.value.trim();
    const travelDate  = dateInput.value;

    document.querySelector('#results-route').textContent = `${source} to ${destination}`;
    document.querySelector('#results-date').textContent  = formatDate(travelDate);
    resultsSection.hidden = false;
    resultsSection.scrollIntoView({ behavior: 'smooth', block: 'start' });

    // Show loading state
    journeyList.innerHTML = `
        <div style="text-align:center;padding:40px;color:#64748b;">
            <div class="spinner" style="border:3px solid #e2e8f0;border-top-color:#2563eb;
                border-radius:50%;width:36px;height:36px;animation:spin 0.7s linear infinite;margin:0 auto 16px;"></div>
            Searching available buses...
        </div>`;

    try {
        // 1. Search routes matching source <-> destination
        const routesRes  = await fetch(`${API_BASE}/routes/search?source=${encodeURIComponent(source)}&destination=${encodeURIComponent(destination)}`);
        const routesData = await routesRes.json();

        if (!routesRes.ok || !routesData.data || routesData.data.length === 0) {
            showNoResults(source, destination);
            return;
        }

        // 2. For each matching route fetch its schedules on the travel date
        const from = `${travelDate}T00:00:00`;
        const to   = `${travelDate}T23:59:00`;

        const schedulePromises = routesData.data.map(route =>
            fetch(`${API_BASE}/schedules/route/${route.id}?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`)
                .then(r => r.json())
        );

        const scheduleResults = await Promise.all(schedulePromises);
        const allSchedules = scheduleResults.flatMap(r => (r.data && r.data.length > 0 ? r.data : []));

        if (allSchedules.length === 0) {
            lastSearchedSchedules = [];
            showNoResults(source, destination);
            return;
        }

        lastSearchedSchedules = allSchedules;

        // Update count with localization
        const foundWord = window.GoBusI18n ? window.GoBusI18n.t('results.busesFound', 'buses found') : 'buses found';
        const metaSpans = document.querySelectorAll('.results-meta span');
        if (metaSpans && metaSpans.length >= 3) {
            metaSpans[2].textContent = `${allSchedules.length} ${foundWord}`;
        }

        // Render schedule cards
        journeyList.innerHTML = allSchedules.map(s => buildScheduleCard(s)).join('');

        if (window.lucide) lucide.createIcons();

        // Attach Choose seats handlers
        attachChooseBtnHandlers();

    } catch (err) {
        console.error('Search error:', err);
        journeyList.innerHTML = `
            <div style="text-align:center;padding:40px;color:#ef4444;">
                <p>Could not reach the API. Make sure the Spring Boot server is running on port 8080.</p>
            </div>`;
    }
});

function getBusDetails(s) {
    const name = (s.busName || '').toLowerCase();
    const id = Math.abs(Number(s.busId || s.id || 0));

    if (name.includes('southern')) {
        return {
            image: 'images/buses/bus-southern-lines.jpg',
            tag: 'DOUBLE DECKER'
        };
    }
    if (name.includes('lanka')) {
        return {
            image: 'images/buses/bus-express-lanka.jpg',
            tag: 'SUPER LUXURY'
        };
    }
    if (name.includes('highway') || name.includes('superline') || name.includes('cruiser')) {
        return {
            image: 'images/buses/bus-highway-superline.jpg',
            tag: 'VIP SLEEPER'
        };
    }
    if (name.includes('gobus')) {
        return {
            image: 'images/buses/bus-gobus-express.jpg',
            tag: 'EXECUTIVE AC'
        };
    }

    const busCatalog = [
        { image: 'images/buses/bus-gobus-express.jpg', tag: 'EXECUTIVE AC' },
        { image: 'images/buses/bus-southern-lines.jpg', tag: 'DOUBLE DECKER' },
        { image: 'images/buses/bus-express-lanka.jpg', tag: 'SUPER LUXURY' },
        { image: 'images/buses/bus-highway-superline.jpg', tag: 'VIP SLEEPER' }
    ];
    return busCatalog[id % busCatalog.length];
}

let lastSearchedSchedules = [];

function buildScheduleCard(s) {
    const dep   = new Date(s.departureTime);
    const arr   = new Date(s.arrivalTime);
    const depTime = dep.toTimeString().slice(0, 5);
    const arrTime = arr.toTimeString().slice(0, 5);
    const diffMs  = arr - dep;
    const hours   = Math.floor(diffMs / 3600000);
    const mins    = Math.round((diffMs % 3600000) / 60000);
    const duration = `${hours}h ${mins}m`;
    const price   = Number(s.fare).toLocaleString('en-LK');
    const seats   = s.availableSeats;
    const busInfo = getBusDetails(s);

    const t = (key, fallback) => (window.GoBusI18n ? window.GoBusI18n.t(key, fallback) : fallback);
    const seatsText = `${seats} ${t('results.seatsLeft', 'seats left')}`;
    const fromLabel = t('results.fromPrice', 'from');
    const chooseBtnText = seats > 0 ? t('results.chooseSeats', 'Choose seats') : t('results.soldOut', 'Sold out');

    return `
    <article class="journey-card" data-schedule-id="${s.id}" data-route="${s.source} -> ${s.destination}" data-dep="${depTime}" data-arr="${arrTime}">
        <div class="bus-thumb-wrapper">
            <img src="${busInfo.image}" alt="${s.busName || 'Luxury Bus'}" class="bus-thumb-img" loading="lazy">
            <span class="bus-thumb-badge">${busInfo.tag}</span>
        </div>
        <div class="journey-main">
            <div>
                <h3>${s.busName || s.busNumber} <span class="rating"><i data-lucide="armchair"></i> ${seatsText}</span></h3>
                <p>${s.source} · ${s.busNumber}</p>
            </div>
            <div class="journey-times">
                <strong>${depTime}</strong>
                <span>${duration}</span>
                <strong>${arrTime}</strong>
            </div>
        </div>
        <div class="journey-price">
            <span>${fromLabel}</span>
            <strong>Rs. ${price}</strong>
            <button class="choose-btn" data-schedule-id="${s.id}" data-journey="${s.busName || s.busNumber}"
                    data-route="${s.source} -> ${s.destination}" data-dep="${depTime}" data-arr="${arrTime}"
                    data-seats="${seats}" data-price="${s.fare}" type="button">
                ${chooseBtnText}
            </button>
        </div>
    </article>`;
}

function showNoResults(source, destination) {
    const t = (key, fallback) => (window.GoBusI18n ? window.GoBusI18n.t(key, fallback) : fallback);
    const title = t('results.noBuses', 'No buses found');
    const hint = t('results.noBusesHint', 'Try another date or check available routes below.');
    const msg = t('results.noBusesMsg', `No scheduled trips from ${source} to ${destination} on this date.`)
        .replace('{source}', source)
        .replace('{destination}', destination);

    journeyList.innerHTML = `
        <div style="text-align:center;padding:48px 20px;color:#64748b;">
            <i data-lucide="bus-front" style="width:48px;height:48px;margin-bottom:12px;opacity:0.4;"></i>
            <h3 style="color:#1e293b;margin-bottom:8px;">${title}</h3>
            <p>${msg}</p>
            <p style="margin-top:8px;font-size:0.9em;">${hint}</p>
        </div>`;
    if (window.lucide) lucide.createIcons();
    const countSpan = document.querySelectorAll('.results-meta span')[2];
    if (countSpan) countSpan.textContent = `0 ${t('results.busesFound', 'buses found')}`;
}

window.addEventListener('gobus:languageChanged', () => {
    if (lastSearchedSchedules && lastSearchedSchedules.length > 0) {
        journeyList.innerHTML = lastSearchedSchedules.map(s => buildScheduleCard(s)).join('');
        if (window.lucide) lucide.createIcons();
        attachChooseBtnHandlers();
        const foundLabel = window.GoBusI18n ? window.GoBusI18n.t('results.busesFound', 'buses found') : 'buses found';
        const countSpan = document.querySelectorAll('.results-meta span')[2];
        if (countSpan) countSpan.textContent = `${lastSearchedSchedules.length} ${foundLabel}`;
    }
});

// -- Edit search ---------------------------------------------------------------
document.querySelector('#edit-search').addEventListener('click', () =>
    document.querySelector('#search-card').scrollIntoView({ behavior: 'smooth' }));

// -- Seat Modal ----------------------------------------------------------------
const modal           = document.querySelector('#seat-modal');
const seatGrid        = document.querySelector('#seat-grid');
const selectedSeatLbl = document.querySelector('#selected-seat-label');
const continueButton  = document.querySelector('#continue-btn');
let selectedSeat      = null;
let activeSchedule    = null;

function buildSeatGrid(seatsList = []) {
    seatGrid.innerHTML = '';
    if (!seatsList || seatsList.length === 0) {
        seatGrid.innerHTML = '<p style="grid-column:1/-1;text-align:center;color:#64748b;padding:20px 0;">No seat layout found</p>';
        return;
    }
    seatsList.forEach(s => {
        const seat = document.createElement('button');
        seat.type  = 'button';
        const isOccupied = s.booked;
        seat.className   = `seat ${isOccupied ? 'occupied' : 'available'}`;
        seat.setAttribute('aria-label', `Seat ${s.seatNumber}`);
        seat.dataset.seatId = s.id;
        seat.dataset.seatNumber = s.seatNumber;
        seat.title = `Seat ${s.seatNumber} (${s.seatType || 'SEAT'}${isOccupied ? ' - Booked' : ''})`;

        if (!isOccupied) {
            seat.addEventListener('click', () => {
                document.querySelectorAll('.seat.selected')
                    .forEach(el => el.classList.replace('selected', 'available'));
                seat.classList.replace('available', 'selected');
                selectedSeat = { id: s.id, number: s.seatNumber };
                selectedSeatLbl.textContent = `Seat ${s.seatNumber}`;
                continueButton.disabled = false;
            });
        }
        seatGrid.appendChild(seat);
    });
}

function attachChooseBtnHandlers() {
    document.querySelectorAll('.choose-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            if (Number(btn.dataset.seats) === 0) return;

            const token = localStorage.getItem('token');
            if (!token) {
                localStorage.setItem('redirectAfterLogin', 'index.html');
                window.location.href = 'login.html';
                return;
            }

            activeSchedule = {
                id      : btn.dataset.scheduleId,
                journey : btn.dataset.journey,
                route   : btn.dataset.route,
                dep     : btn.dataset.dep,
                arr     : btn.dataset.arr,
                price   : btn.dataset.price,
            };

            document.querySelector('#seat-title').textContent   = btn.dataset.journey;
            document.querySelector('.modal-subtitle').textContent =
                `${btn.dataset.route} · ${btn.dataset.dep} - ${btn.dataset.arr}`;

            selectedSeat = null;
            selectedSeatLbl.textContent = 'Choose a seat';
            continueButton.disabled = true;
            seatGrid.innerHTML = '<p style="grid-column:1/-1;text-align:center;color:#64748b;padding:20px 0;">Loading seats...</p>';

            modal.hidden = false;
            document.body.style.overflow = 'hidden';
            if (window.lucide) lucide.createIcons();

            try {
                const res = await fetch(`${API_BASE}/schedules/${btn.dataset.scheduleId}/seats`);
                const data = await res.json();
                buildSeatGrid(data.data || []);
            } catch (err) {
                console.error('Error fetching seats:', err);
                seatGrid.innerHTML = '<p style="grid-column:1/-1;text-align:center;color:#ef4444;padding:20px 0;">Failed to load seat layout</p>';
            }
        });
    });
}

function closeModal() {
    modal.hidden = true;
    document.body.style.overflow = '';
}

document.querySelector('#close-modal').addEventListener('click', closeModal);
modal.addEventListener('click', e => { if (e.target === modal) closeModal(); });

continueButton.addEventListener('click', async () => {
    if (!selectedSeat || !activeSchedule) return;

    // Redirect to booking confirmation storing selected schedule & seat
    localStorage.setItem('pendingBooking', JSON.stringify({
        scheduleId : activeSchedule.id,
        journey    : activeSchedule.journey,
        route      : activeSchedule.route,
        dep        : activeSchedule.dep,
        arr        : activeSchedule.arr,
        seatId     : selectedSeat.id,
        seatNumber : selectedSeat.number,
        price      : activeSchedule.price,
    }));

    window.location.href = 'booking-confirmation.html';
});

// CSS keyframe for spinner
const style = document.createElement('style');
style.textContent = '@keyframes spin { to { transform: rotate(360deg); } }';
document.head.appendChild(style);

// -- Contact Support Form -----------------------------------------------------
const contactForm = document.getElementById('contact-support-form');
if (contactForm) {
    contactForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('c-name')?.value.trim();
        const email = document.getElementById('c-email')?.value.trim();
        const phone = document.getElementById('c-phone')?.value.trim();
        const subject = document.getElementById('c-subject')?.value;
        const message = document.getElementById('c-message')?.value.trim();
        const feedback = document.getElementById('contact-feedback');
        const submitBtn = document.getElementById('c-submit-btn');

        if (!name || !email || !phone || !subject || !message) {
            if (feedback) {
                feedback.className = 'contact-feedback-msg';
                feedback.style.display = 'block';
                feedback.style.background = '#fef2f2';
                feedback.style.border = '1px solid #fecaca';
                feedback.style.color = '#991b1b';
                feedback.textContent = 'Please fill out all required fields before submitting.';
            }
            return;
        }

        submitBtn.disabled = true;
        submitBtn.innerHTML = 'Sending…';

        setTimeout(() => {
            contactForm.reset();
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i data-lucide="send"></i> Send Inquiry';
            if (window.lucide) lucide.createIcons();

            if (feedback) {
                feedback.className = 'contact-feedback-msg success';
                feedback.style.display = 'block';
                feedback.innerHTML = `<strong>Thank you, ${name}!</strong> Your inquiry has been received (Ref: <strong>#GB-${Math.floor(100000 + Math.random() * 900000)}</strong>). Our 24/7 passenger care team will contact you via email (${email}) or phone shortly.`;
            }
        }, 600);
    });
}

