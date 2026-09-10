package Bus_Ticket_booking.ijse.Service.Serviceimpl;

import Bus_Ticket_booking.ijse.DTO.request.CancellationRequest;
import Bus_Ticket_booking.ijse.DTO.response.CancellationResponse;
import Bus_Ticket_booking.ijse.Service.CancellationService;
import Bus_Ticket_booking.ijse.Service.NotificationService;
import Bus_Ticket_booking.ijse.entity.Booking;
import Bus_Ticket_booking.ijse.entity.Cancellation;
import Bus_Ticket_booking.ijse.entity.User;
import Bus_Ticket_booking.ijse.enumaration.BookingStatus;
import Bus_Ticket_booking.ijse.enumaration.CancellationStatus;
import Bus_Ticket_booking.ijse.exception.BadRequestException;
import Bus_Ticket_booking.ijse.exception.ResourceNotFoundException;
import Bus_Ticket_booking.ijse.exception.UnauthorizedException;
import Bus_Ticket_booking.ijse.repository.BookingRepository;
import Bus_Ticket_booking.ijse.repository.CancellationRepository;
import Bus_Ticket_booking.ijse.repository.ScheduleRepository;
import Bus_Ticket_booking.ijse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancellationServiceImpl implements CancellationService {

    private final CancellationRepository cancellationRepository;
    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CancellationResponse requestCancellation(CancellationRequest request, String username) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.getBookingId()));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not authorized to cancel this booking");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }
        if (cancellationRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new BadRequestException("Cancellation already requested for this booking");
        }

        // 80% refund policy
        BigDecimal refundAmount = booking.getTotalAmount().multiply(BigDecimal.valueOf(0.80));

        Cancellation cancellation = Cancellation.builder()
                .booking(booking)
                .reason(request.getReason())
                .refundAmount(refundAmount)
                .status(CancellationStatus.APPROVED)
                .build();

        Cancellation saved = cancellationRepository.save(cancellation);

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Restore seat count
        var schedule = booking.getSchedule();
        int seatsToRestore = booking.getBookingSeats().size();
        schedule.setAvailableSeats(schedule.getAvailableSeats() + seatsToRestore);
        scheduleRepository.save(schedule);

        // Notify user
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            notificationService.sendCancellationConfirmed(user, saved);
        }

        log.info("Cancellation {} processed for booking: {}", saved.getId(), booking.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CancellationResponse> getAllCancellations() {
        return cancellationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CancellationResponse getCancellationByBookingId(Long bookingId) {
        Cancellation cancellation = cancellationRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Cancellation", "bookingId", bookingId));
        return toResponse(cancellation);
    }

    @Override
    public CancellationResponse toResponse(Cancellation c) {
        return CancellationResponse.builder()
                .id(c.getId())
                .bookingId(c.getBooking().getId())
                .cancellationDate(c.getCancellationDate())
                .reason(c.getReason())
                .refundAmount(c.getRefundAmount())
                .status(c.getStatus())
                .build();
    }
}
