package Bus_Ticket_booking.ijse.Service.Serviceimpl;

import Bus_Ticket_booking.ijse.DTO.request.BookingRequest;
import Bus_Ticket_booking.ijse.DTO.response.BookingResponse;
import Bus_Ticket_booking.ijse.Service.BookingService;
import Bus_Ticket_booking.ijse.Service.NotificationService;
import Bus_Ticket_booking.ijse.entity.*;
import Bus_Ticket_booking.ijse.enumaration.BookingStatus;
import Bus_Ticket_booking.ijse.exception.BadRequestException;
import Bus_Ticket_booking.ijse.exception.ResourceNotFoundException;
import Bus_Ticket_booking.ijse.exception.UnauthorizedException;
import Bus_Ticket_booking.ijse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final BusSeatRepository busSeatRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", request.getScheduleId()));

        if (schedule.getAvailableSeats() < request.getSeatIds().size()) {
            throw new BadRequestException("Not enough seats available. Requested: "
                    + request.getSeatIds().size() + ", Available: " + schedule.getAvailableSeats());
        }

        // Validate seats not already booked for this schedule
        for (Long seatId : request.getSeatIds()) {
            if (bookingSeatRepository.existsByBusSeatIdAndBookingScheduleId(seatId, schedule.getId())) {
                throw new BadRequestException("Seat id " + seatId + " is already booked for this schedule");
            }
        }

        // Build passenger
        Passenger passenger = Passenger.builder()
                .firstName(request.getPassenger().getFirstName())
                .lastName(request.getPassenger().getLastName())
                .nic(request.getPassenger().getNic())
                .age(request.getPassenger().getAge())
                .gender(request.getPassenger().getGender())
                .phone(request.getPassenger().getPhone())
                .build();

        // Calculate total amount
        var totalAmount = schedule.getFare().multiply(
                java.math.BigDecimal.valueOf(request.getSeatIds().size()));

        // Build booking
        Booking booking = Booking.builder()
                .user(user)
                .schedule(schedule)
                .passenger(passenger)
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Reserve seats
        List<BusSeat> seats = busSeatRepository.findAllById(request.getSeatIds());
        if (seats.size() != request.getSeatIds().size()) {
            throw new BadRequestException("One or more seat IDs are invalid");
        }

        List<BookingSeat> bookingSeats = seats.stream()
                .map(seat -> BookingSeat.builder().booking(savedBooking).busSeat(seat).build())
                .collect(Collectors.toList());
        bookingSeatRepository.saveAll(bookingSeats);

        // Decrement available seats
        schedule.setAvailableSeats(schedule.getAvailableSeats() - request.getSeatIds().size());
        scheduleRepository.save(schedule);

        // Send notification
        notificationService.sendBookingConfirmation(user, savedBooking);

        log.info("Booking {} created for user: {}", savedBooking.getId(), username);
        return toResponse(savedBooking, bookingSeats);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(b -> toResponse(b, bookingSeatRepository.findByBookingId(b.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id, String username) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        // Users can only view their own bookings
        if (!booking.getUser().getUsername().equals(username)
                && !isAdmin(userRepository.findByUsername(username).orElse(null))) {
            throw new UnauthorizedException("You are not authorized to view this booking");
        }
        return toResponse(booking, bookingSeatRepository.findByBookingId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(b -> toResponse(b, bookingSeatRepository.findByBookingId(b.getId())))
                .collect(Collectors.toList());
    }

    private boolean isAdmin(User user) {
        if (user == null) return false;
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().name().equals("ROLE_ADMIN"));
    }

    @Override
    public BookingResponse toResponse(Booking booking, List<BookingSeat> bookingSeats) {
        List<String> seatNumbers = bookingSeats.stream()
                .map(bs -> bs.getBusSeat().getSeatNumber())
                .collect(Collectors.toList());
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .username(booking.getUser().getUsername())
                .scheduleId(booking.getSchedule().getId())
                .source(booking.getSchedule().getRoute().getSource())
                .destination(booking.getSchedule().getRoute().getDestination())
                .departureTime(booking.getSchedule().getDepartureTime())
                .arrivalTime(booking.getSchedule().getArrivalTime())
                .busName(booking.getSchedule().getBus() != null ? booking.getSchedule().getBus().getBusName() : null)
                .busNumber(booking.getSchedule().getBus() != null ? booking.getSchedule().getBus().getBusNumber() : null)
                .passengerName(booking.getPassenger().getFirstName() + " " + booking.getPassenger().getLastName())
                .passengerNic(booking.getPassenger().getNic())
                .passengerPhone(booking.getPassenger().getPhone())
                .seatNumbers(seatNumbers)
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .build();
    }
}
