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
 * Seeds the database with sample data on first startup.
 * Roles → Users → Routes → Buses → Seats → Schedules
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository     roleRepository;
    private final UserRepository     userRepository;
    private final RouteRepository    routeRepository;
    private final BusRepository      busRepository;
    private final BusSeatRepository  busSeatRepository;
    private final ScheduleRepository scheduleRepository;
    private final PasswordEncoder    passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("=== GoBus API – Data Initialization ===");

        // ── Roles ────────────────────────────────────────────────────────────
        Role adminRole = createRoleIfNotExists(RoleName.ROLE_ADMIN);
        Role userRole  = createRoleIfNotExists(RoleName.ROLE_USER);
                         createRoleIfNotExists(RoleName.ROLE_GUEST);

        // ── Users ────────────────────────────────────────────────────────────
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin").email("admin@gobus.lk")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .fullName("GoBus Administrator").phone("+94771234567")
                    .roles(Set.of(adminRole, userRole)).build());
            log.info("Admin user created  → admin / Admin@1234");
        }
        if (!userRepository.existsByUsername("testuser")) {
            userRepository.save(User.builder()
                    .username("testuser").email("user@gobus.lk")
                    .password(passwordEncoder.encode("User@1234"))
                    .fullName("Test User").phone("+94779876543")
                    .roles(Set.of(userRole)).build());
            log.info("Test user created   → testuser / User@1234");
        }

        // ── Routes ───────────────────────────────────────────────────────────
        Route r1 = getOrCreateRoute("Colombo",  "Kandy",   166, 240, new BigDecimal("1550.00"));
        Route r2 = getOrCreateRoute("Colombo",  "Galle",   116, 120, new BigDecimal("950.00"));
        Route r3 = getOrCreateRoute("Colombo",  "Jaffna",  400, 480, new BigDecimal("2450.00"));
        Route r4 = getOrCreateRoute("Kandy",    "Badulla", 90,  150, new BigDecimal("1200.00"));
        Route r5 = getOrCreateRoute("Colombo",  "Matara",  160, 180, new BigDecimal("1100.00"));
        Route r6 = getOrCreateRoute("Kandy",    "Colombo", 166, 240, new BigDecimal("1550.00"));
        Route r7 = getOrCreateRoute("Galle",    "Colombo", 116, 120, new BigDecimal("950.00"));
        Route r8 = getOrCreateRoute("Colombo",  "Nuwara Eliya", 180, 270, new BigDecimal("1750.00"));
        log.info("Ensured {} routes exist", 8);

        // ── Buses ────────────────────────────────────────────────────────────
        Bus b1 = getOrCreateBus("NB-1234", "GoBus Express",    BusType.LUXURY,      36);
        Bus b2 = getOrCreateBus("SL-5678", "Southern Lines",   BusType.AC_SLEEPER,  40);
        Bus b3 = getOrCreateBus("EX-9012", "Express Lanka",    BusType.AC_SEATER,   44);
        Bus b4 = getOrCreateBus("KN-3456", "Kandy Night Liner",BusType.LUXURY,      36);
        Bus b5 = getOrCreateBus("GL-7890", "Galle Premier",    BusType.AC_SEATER,   44);
        log.info("Ensured 5 buses with seats exist");

        // ── Schedules (today + next 7 days) ──────────────────────────────────
        LocalDateTime base = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        if (scheduleRepository.count() == 0 || scheduleRepository.findAllActiveWithSeats().isEmpty()) {
            for (int dayOffset = 0; dayOffset <= 7; dayOffset++) {
                LocalDateTime day = base.plusDays(dayOffset);

                // Colombo → Kandy
                saveSchedule(r1, b1, day.withHour(6).withMinute(30),  day.withHour(10).withMinute(45), new BigDecimal("1850.00"), 36);
                saveSchedule(r1, b2, day.withHour(8).withMinute(0),   day.withHour(12).withMinute(35), new BigDecimal("1550.00"), 40);
                saveSchedule(r1, b3, day.withHour(14).withMinute(0),  day.withHour(18).withMinute(15), new BigDecimal("1650.00"), 44);

                // Colombo → Galle
                saveSchedule(r2, b5, day.withHour(7).withMinute(0),   day.withHour(9).withMinute(0),   new BigDecimal("950.00"),  44);
                saveSchedule(r2, b3, day.withHour(13).withMinute(0),  day.withHour(15).withMinute(0),  new BigDecimal("980.00"),  44);

                // Colombo → Jaffna
                saveSchedule(r3, b1, day.withHour(5).withMinute(0),   day.withHour(13).withMinute(0),  new BigDecimal("2450.00"), 36);
                saveSchedule(r3, b4, day.withHour(20).withMinute(0),  day.plusDays(1).withHour(4).withMinute(0), new BigDecimal("2600.00"), 36);

                // Kandy → Badulla
                saveSchedule(r4, b4, day.withHour(9).withMinute(0),   day.withHour(11).withMinute(30), new BigDecimal("1200.00"), 36);

                // Colombo → Matara
                saveSchedule(r5, b2, day.withHour(7).withMinute(30),  day.withHour(10).withMinute(30), new BigDecimal("1100.00"), 40);

                // Kandy → Colombo (return)
                saveSchedule(r6, b1, day.withHour(15).withMinute(0),  day.withHour(19).withMinute(15), new BigDecimal("1750.00"), 36);

                // Galle → Colombo (return)
                saveSchedule(r7, b5, day.withHour(16).withMinute(0),  day.withHour(18).withMinute(0),  new BigDecimal("950.00"),  44);

                // Colombo → Nuwara Eliya
                saveSchedule(r8, b4, day.withHour(6).withMinute(0),   day.withHour(10).withMinute(30), new BigDecimal("1750.00"), 36);
            }
            log.info("Seeded schedules for today + 7 days");
        }
        log.info("=== Data Initialization Complete ===");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Route getOrCreateRoute(String source, String destination, int distKm, int durationMin, BigDecimal baseFare) {
        List<Route> existing = routeRepository.findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        return saveRoute(source, destination, distKm, durationMin, baseFare);
    }

    private Bus getOrCreateBus(String busNumber, String busName, BusType busType, int totalSeats) {
        return busRepository.findByBusNumber(busNumber).orElseGet(() -> saveBus(busNumber, busName, busType, totalSeats));
    }

    private Route saveRoute(String source, String destination, int distKm, int durationMin, BigDecimal baseFare) {
        Route r = Route.builder()
                .source(source).destination(destination)
                .distanceKm(BigDecimal.valueOf(distKm)).durationMinutes(durationMin)
                .baseFare(baseFare).build();
        return routeRepository.save(r);
    }

    private Bus saveBus(String busNumber, String busName, BusType busType, int totalSeats) {
        Bus bus = Bus.builder()
                .busNumber(busNumber).busName(busName)
                .busType(busType).totalSeats(totalSeats).build();
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
