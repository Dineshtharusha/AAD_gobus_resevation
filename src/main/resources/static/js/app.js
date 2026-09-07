const dateInput = document.querySelector('#travel-date');
const today = new Date();
dateInput.min = today.toISOString().split('T')[0];
dateInput.value = today.toISOString().split('T')[0];

if (window.lucide) lucide.createIcons();

const fromCity = document.querySelector('#from-city');
const toCity = document.querySelector('#to-city');
const searchForm = document.querySelector('#search-form');
const results = document.querySelector('#results');

document.querySelector('#swap-cities').addEventListener('click', () => {
    const currentFrom = fromCity.value;
    fromCity.value = toCity.value;
    toCity.value = currentFrom;
});

document.querySelectorAll('.trip-tab').forEach((tab) => {
    tab.addEventListener('click', () => {
        document.querySelectorAll('.trip-tab').forEach((item) => item.classList.remove('active'));
        tab.classList.add('active');
    });
});

function formatDate(value) {
    return new Intl.DateTimeFormat('en-LK', { day: 'numeric', month: 'short', year: 'numeric' }).format(new Date(`${value}T00:00:00`));
}

searchForm.addEventListener('submit', (event) => {
    event.preventDefault();
    document.querySelector('#results-route').textContent = `${fromCity.value} to ${toCity.value}`;
    document.querySelector('#results-date').textContent = formatDate(dateInput.value);
    results.hidden = false;
    results.scrollIntoView({ behavior: 'smooth', block: 'start' });
});

document.querySelector('#edit-search').addEventListener('click', () => document.querySelector('#search-card').scrollIntoView({ behavior: 'smooth' }));

document.querySelectorAll('.route-card').forEach((route) => {
    route.addEventListener('click', () => {
        fromCity.value = route.dataset.from;
        toCity.value = route.dataset.to;
        document.querySelector('#search-card').scrollIntoView({ behavior: 'smooth', block: 'center' });
    });
});

const modal = document.querySelector('#seat-modal');
const seatGrid = document.querySelector('#seat-grid');
const selectedSeatLabel = document.querySelector('#selected-seat-label');
const continueButton = document.querySelector('#continue-btn');
let selectedSeat = null;

for (let row = 1; row <= 8; row += 1) {
    for (let column = 1; column <= 4; column += 1) {
        const seatNumber = (row - 1) * 4 + column;
        const seat = document.createElement('button');
        seat.type = 'button';
        seat.className = `seat ${[3, 8, 14, 21, 25].includes(seatNumber) ? 'occupied' : 'available'}`;
        seat.setAttribute('aria-label', `Seat ${seatNumber}`);
        seat.dataset.seat = seatNumber;
        seat.addEventListener('click', () => {
            if (seat.classList.contains('occupied')) return;
            document.querySelectorAll('.seat.selected').forEach((item) => item.classList.replace('selected', 'available'));
            seat.classList.replace('available', 'selected');
            selectedSeat = seatNumber;
            selectedSeatLabel.textContent = `Seat ${seatNumber}`;
            continueButton.disabled = false;
        });
        seatGrid.appendChild(seat);
    }
}

document.querySelectorAll('.choose-btn').forEach((button) => {
    button.addEventListener('click', () => {
        document.querySelector('#seat-title').textContent = button.dataset.journey;
        modal.hidden = false;
        document.body.style.overflow = 'hidden';
    });
});

function closeModal() {
    modal.hidden = true;
    document.body.style.overflow = '';
}

document.querySelector('#close-modal').addEventListener('click', closeModal);
modal.addEventListener('click', (event) => { if (event.target === modal) closeModal(); });
continueButton.addEventListener('click', () => {
    continueButton.innerHTML = `Seat ${selectedSeat} reserved <i data-lucide="check"></i>`;
    continueButton.disabled = true;
    if (window.lucide) lucide.createIcons();
});