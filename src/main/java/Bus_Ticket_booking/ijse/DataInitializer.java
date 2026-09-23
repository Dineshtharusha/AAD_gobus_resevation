package Bus_Ticket_booking.ijse;

import Bus_Ticket_booking.ijse.entity.*;
import Bus_Ticket_booking.ijse.enumaration.*;
import Bus_Ticket_booking.ijse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Seeds the database with sample data on startup.
 * Roles → Users (Admin, User, Bus Owner) → Routes → Buses → Seats → Schedules → Sample Bookings
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository        roleRepository;
    private final UserRepository        userRepository;
    private final RouteRepository       routeRepository;
    private final BusRepository         busRepository;
    private final BusSeatRepository     busSeatRepository;
    private final ScheduleRepository    scheduleRepository;
    private final BookingRepository     bookingRepository;
    private final BookingSeatRepository  bookingSeatRepository;
    private final PassengerRepository    passengerRepository;
    private final NotificationRepository notificationRepository;
    private final ReviewRepository       reviewRepository;
    private final PasswordEncoder       passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("=== GoBus API – Data Initialization ===");

        // ── Roles ────────────────────────────────────────────────────────────
        Role adminRole = createRoleIfNotExists(RoleName.ROLE_ADMIN);
        Role userRole  = createRoleIfNotExists(RoleName.ROLE_USER);
        Role ownerRole = createRoleIfNotExists(RoleName.ROLE_BUS_OWNER);
                         createRoleIfNotExists(RoleName.ROLE_GUEST);

        // ── Users (Guarantee presence, correct roles, enabled=true, and standard passwords) ──
        User adminUser = userRepository.findByUsername("admin").orElse(null);
        if (adminUser == null) {
            adminUser = userRepository.save(User.builder()
                    .username("admin").email("admin@gobus.lk")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .fullName("GoBus Administrator").phone("+94771234567")
                    .enabled(true)
                    .roles(Set.of(adminRole, userRole)).build());
            log.info("Admin user created      → admin / Admin@1234");
        } else {
            adminUser.setEnabled(true);
            adminUser.setPassword(passwordEncoder.encode("Admin@1234"));
            adminUser.getRoles().add(adminRole);
            adminUser.getRoles().add(userRole);
            adminUser = userRepository.save(adminUser);
            log.info("Admin user verified     → admin / Admin@1234");
        }

        User testUser = userRepository.findByUsername("testuser").orElse(null);
        if (testUser == null) {
            testUser = userRepository.save(User.builder()
                    .username("testuser").email("user@gobus.lk")
                    .password(passwordEncoder.encode("User@1234"))
                    .fullName("Test User").phone("+94779876543")
                    .enabled(true)
                    .roles(Set.of(userRole)).build());
            log.info("Test user created       → testuser / User@1234");
        } else {
            testUser.setEnabled(true);
            testUser.setPassword(passwordEncoder.encode("User@1234"));
            testUser.getRoles().add(userRole);
            testUser = userRepository.save(testUser);
            log.info("Test user verified      → testuser / User@1234");
        }

        User busOwner = userRepository.findByUsername("busowner").orElse(null);
        if (busOwner == null) {
            busOwner = userRepository.save(User.builder()
                    .username("busowner").email("owner@gobus.lk")
                    .password(passwordEncoder.encode("Owner@1234"))
                    .fullName("Kamal Gunasekara (Bus Operator)").phone("+94773344556")
                    .enabled(true)
                    .roles(Set.of(ownerRole, userRole)).build());
            log.info("Bus Owner user created  → busowner / Owner@1234");
        } else {
            busOwner.setEnabled(true);
            busOwner.setPassword(passwordEncoder.encode("Owner@1234"));
            busOwner.getRoles().add(ownerRole);
            busOwner.getRoles().add(userRole);
            busOwner = userRepository.save(busOwner);
            log.info("Bus Owner verified      → busowner / Owner@1234");
        }

        // ── Ensure all existing registered users are enabled ─────────────────
        userRepository.findAll().forEach(u -> {
            if (!u.isEnabled()) {
                u.setEnabled(true);
                userRepository.save(u);
                log.info("Auto-activated user account: {}", u.getUsername());
            }
        });

        // ── Routes ───────────────────────────────────────────────────────────
        Route r1 = getOrCreateRoute("Colombo",  "Kandy",   166, 240, new BigDecimal("1550.00"));
        Route r2 = getOrCreateRoute("Colombo",  "Galle",   116, 120, new BigDecimal("950.00"));
        Route r3 = getOrCreateRoute("Colombo",  "Jaffna",  400, 480, new BigDecimal("2450.00"));
        Route r4 = getOrCreateRoute("Kandy",    "Badulla", 90,  150, new BigDecimal("1200.00"));
        Route r5 = getOrCreateRoute("Colombo",  "Matara",  160, 180, new BigDecimal("1100.00"));
        Route r6 = getOrCreateRoute("Kandy",    "Colombo", 166, 240, new BigDecimal("1550.00"));
        Route r7 = getOrCreateRoute("Galle",    "Colombo", 116, 120, new BigDecimal("950.00"));
        Route r8 = getOrCreateRoute("Colombo",  "Nuwara Eliya", 180, 270, new BigDecimal("1750.00"));
        log.info("Ensured 8 routes exist");

        // ── Buses (Assigned to Bus Owner) ────────────────────────────────────
        Bus b1 = getOrCreateBus("NB-1234", "GoBus Express",     BusType.LUXURY,      36, busOwner);
        Bus b2 = getOrCreateBus("SL-5678", "Southern Lines",    BusType.AC_SLEEPER,  40, busOwner);
        Bus b3 = getOrCreateBus("EX-9012", "Express Lanka",     BusType.AC_SEATER,   44, busOwner);
        Bus b4 = getOrCreateBus("KN-3456", "Kandy Night Liner", BusType.LUXURY,      36, busOwner);
        Bus b5 = getOrCreateBus("GL-7890", "Galle Premier",     BusType.AC_SEATER,   44, busOwner);
        log.info("Ensured 5 buses with seats and operator assignment exist");

        // ── Schedules (Seed today + next 14 days) ────────────────────────────
        LocalDateTime startOfToday = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        long upcomingCount = scheduleRepository.findAll().stream()
                .filter(s -> s.isActive() && s.getDepartureTime() != null && !s.getDepartureTime().isBefore(startOfToday))
                .count();

        if (upcomingCount < 30) {
            for (int dayOffset = 0; dayOffset <= 14; dayOffset++) {
                LocalDateTime day = startOfToday.plusDays(dayOffset);

                // Colombo → Kandy
                saveScheduleIfNotExists(r1, b1, day.withHour(6).withMinute(30),  day.withHour(10).withMinute(45), new BigDecimal("1850.00"), 36);
                saveScheduleIfNotExists(r1, b2, day.withHour(8).withMinute(0),   day.withHour(12).withMinute(35), new BigDecimal("1550.00"), 40);
                saveScheduleIfNotExists(r1, b3, day.withHour(14).withMinute(0),  day.withHour(18).withMinute(15), new BigDecimal("1650.00"), 44);

                // Colombo → Galle
                saveScheduleIfNotExists(r2, b5, day.withHour(7).withMinute(0),   day.withHour(9).withMinute(0),   new BigDecimal("950.00"),  44);
                saveScheduleIfNotExists(r2, b3, day.withHour(13).withMinute(0),  day.withHour(15).withMinute(0),  new BigDecimal("980.00"),  44);

                // Colombo → Jaffna
                saveScheduleIfNotExists(r3, b1, day.withHour(5).withMinute(0),   day.withHour(13).withMinute(0),  new BigDecimal("2450.00"), 36);
                saveScheduleIfNotExists(r3, b4, day.withHour(20).withMinute(0),  day.plusDays(1).withHour(4).withMinute(0), new BigDecimal("2600.00"), 36);

                // Kandy → Badulla
                saveScheduleIfNotExists(r4, b4, day.withHour(9).withMinute(0),   day.withHour(11).withMinute(30), new BigDecimal("1200.00"), 36);

                // Colombo → Matara
                saveScheduleIfNotExists(r5, b2, day.withHour(7).withMinute(30),  day.withHour(10).withMinute(30), new BigDecimal("1100.00"), 40);

                // Kandy → Colombo (return)
                saveScheduleIfNotExists(r6, b1, day.withHour(15).withMinute(0),  day.withHour(19).withMinute(15), new BigDecimal("1750.00"), 36);

                // Galle → Colombo (return)
                saveScheduleIfNotExists(r7, b5, day.withHour(16).withMinute(0),  day.withHour(18).withMinute(0),  new BigDecimal("950.00"),  44);

                // Colombo → Nuwara Eliya
                saveScheduleIfNotExists(r8, b4, day.withHour(6).withMinute(0),   day.withHour(10).withMinute(30), new BigDecimal("1750.00"), 36);
            }
            log.info("Seeded active schedules for today + next 14 days");
        }

        // ── Seed Sample Bookings for Manifest & Test User ─────────────────────
        List<Booking> testUserBookings = bookingRepository.findByUserId(testUser.getId());
        boolean hasUpcomingConfirmed = testUserBookings.stream().anyMatch(b ->
                b.getStatus() == BookingStatus.CONFIRMED &&
                b.getSchedule() != null &&
                b.getSchedule().getDepartureTime() != null &&
                !b.getSchedule().getDepartureTime().isBefore(LocalDateTime.now()));

        if (!hasUpcomingConfirmed) {
            List<Schedule> upcomingSchedules = scheduleRepository.findAll().stream()
                    .filter(s -> s.isActive() && s.getDepartureTime() != null &&
                            s.getDepartureTime().isAfter(LocalDateTime.now().plusHours(3)) &&
                            s.getAvailableSeats() > 3)
                    .sorted(java.util.Comparator.comparing(Schedule::getDepartureTime))
                    .toList();

            if (!upcomingSchedules.isEmpty()) {
                Schedule s1 = upcomingSchedules.get(0);
                Schedule s2 = upcomingSchedules.size() > 1 ? upcomingSchedules.get(1) : s1;
                createSampleBooking(testUser, s1, "Kasun", "Silva", "199512345678", 29, "Male", "+94779876543", List.of("1", "2"));
                createSampleBooking(testUser, s2, "Kasun", "Silva", "199512345678", 29, "Male", "+94779876543", List.of("5"));
                log.info("Seeded upcoming confirmed bookings for testuser on schedules #{} and #{}", s1.getId(), s2.getId());
            }
        }

        // ── Seed Sample Notification for Test User ────────────────────────────
        if (notificationRepository.findByUserIdAndReadFalse(testUser.getId()).isEmpty()) {
            notificationRepository.save(Notification.builder()
                    .user(testUser)
                    .message("Welcome to GoBus! Your account is active. Explore routes and book tickets instantly.")
                    .type(NotificationType.GENERAL)
                    .read(false)
                    .createdAt(LocalDateTime.now())
                    .build());
            log.info("Seeded welcome notification for testuser");
        }

        // ── Seed Sample Review for Test User ─────────────────────────────────
        if (reviewRepository.findByUserId(testUser.getId()).isEmpty()) {
            reviewRepository.save(Review.builder()
                    .user(testUser)
                    .route(r1)
                    .rating(5)
                    .comment("Great comfort and timely service between Colombo and Kandy!")
                    .createdAt(LocalDateTime.now())
                    .build());
            log.info("Seeded sample review for testuser");
        }

        log.info("=== Data Initialization Complete ===");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void createSampleBooking(User user, Schedule schedule, String fName, String lName,
                                      String nic, int age, String gender, String phone, List<String> seatNums) {
        Passenger passenger = Passenger.builder()
                .firstName(fName)
                .lastName(lName)
                .nic(nic)
                .age(age)
                .gender(gender)
                .phone(phone)
                .build();
        Passenger savedPassenger = passengerRepository.save(passenger);

        BigDecimal farePerSeat = schedule.getFare() != null ? schedule.getFare() : new BigDecimal("1500.00");
        BigDecimal total = farePerSeat.multiply(BigDecimal.valueOf(seatNums.size()));

        Booking booking = Booking.builder()
                .user(user)
                .schedule(schedule)
                .passenger(savedPassenger)
                .bookingDate(LocalDateTime.now().minusHours(2))
                .status(BookingStatus.CONFIRMED)
                .totalAmount(total)
                .build();
        Booking savedBooking = bookingRepository.save(booking);

        List<BusSeat> seats = busSeatRepository.findByBusId(schedule.getBus().getId());
        for (String seatNum : seatNums) {
            BusSeat matched = seats.stream().filter(s -> s.getSeatNumber().equals(seatNum)).findFirst().orElse(null);
            if (matched != null) {
                bookingSeatRepository.save(BookingSeat.builder()
                        .booking(savedBooking)
                        .busSeat(matched)
                        .build());
            }
        }

        // Update schedule available seats
        schedule.setAvailableSeats(Math.max(0, schedule.getAvailableSeats() - seatNums.size()));
        scheduleRepository.save(schedule);
    }

    private Route getOrCreateRoute(String source, String destination, int distKm, int durationMin, BigDecimal baseFare) {
        List<Route> existing = routeRepository.findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        return saveRoute(source, destination, distKm, durationMin, baseFare);
    }

    private Bus getOrCreateBus(String busNumber, String busName, BusType busType, int totalSeats, User owner) {
        return busRepository.findByBusNumber(busNumber)
                .map(existingBus -> {
                    if (existingBus.getOwner() == null && owner != null) {
                        existingBus.setOwner(owner);
                        return busRepository.save(existingBus);
                    }
                    return existingBus;
                })
                .orElseGet(() -> saveBus(busNumber, busName, busType, totalSeats, owner));
    }

    private Route saveRoute(String source, String destination, int distKm, int durationMin, BigDecimal baseFare) {
        Route r = Route.builder()
                .source(source).destination(destination)
                .distanceKm(BigDecimal.valueOf(distKm)).durationMinutes(durationMin)
                .baseFare(baseFare).build();
        return routeRepository.save(r);
    }

    private Bus saveBus(String busNumber, String busName, BusType busType, int totalSeats, User owner) {
        Bus bus = Bus.builder()
                .busNumber(busNumber).busName(busName)
                .busType(busType).totalSeats(totalSeats)
                .owner(owner)
                .build();
        Bus saved = busRepository.save(bus);

        List<BusSeat> seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            seats.add(BusSeat.builder()
                    .bus(saved)
                    .seatNumber(String.valueOf(i))
                    .seatType(i % 4 == 0 ? SeatType.AISLE : SeatType.WINDOW)
                    .build());
        }
        busSeatRepository.saveAll(seats);
        return saved;
    }

    private void saveScheduleIfNotExists(Route route, Bus bus, LocalDateTime dep, LocalDateTime arr,
                                         BigDecimal fare, int availableSeats) {
        if (!scheduleRepository.existsByRouteIdAndBusIdAndDepartureTime(route.getId(), bus.getId(), dep)) {
            saveSchedule(route, bus, dep, arr, fare, availableSeats);
        }
    }

    private void saveSchedule(Route route, Bus bus, LocalDateTime dep, LocalDateTime arr,
                               BigDecimal fare, int availableSeats) {
        scheduleRepository.save(Schedule.builder()
                .route(route).bus(bus)
                .departureTime(dep).arrivalTime(arr)
                .fare(fare).availableSeats(availableSeats).build());
    }

    private Role createRoleIfNotExists(RoleName name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = Role.builder().name(name).build();
            roleRepository.save(role);
            log.info("Created role: {}", name);
            return role;
        });
    }
}
