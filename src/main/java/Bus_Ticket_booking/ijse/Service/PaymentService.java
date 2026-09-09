package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.PaymentRequest;
import Bus_Ticket_booking.ijse.DTO.response.PaymentResponse;
import Bus_Ticket_booking.ijse.entity.Booking;
import Bus_Ticket_booking.ijse.entity.Payment;
import Bus_Ticket_booking.ijse.entity.User;
import Bus_Ticket_booking.ijse.enumaration.BookingStatus;
import Bus_Ticket_booking.ijse.enumaration.PaymentStatus;
import Bus_Ticket_booking.ijse.exception.BadRequestException;
import Bus_Ticket_booking.ijse.exception.ResourceNotFoundException;
import Bus_Ticket_booking.ijse.repository.BookingRepository;
import Bus_Ticket_booking.ijse.repository.PaymentRepository;
import Bus_Ticket_booking.ijse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, String username) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.getBookingId()));

        if (paymentRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new BadRequestException("Payment already processed for booking id: " + booking.getId());
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot process payment for a cancelled booking");
        }

        // Simulate payment processing (in real system, call payment gateway)
        String transactionId = "TXN-" + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 12);

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .status(PaymentStatus.SUCCESS)
                .method(request.getMethod())
                .transactionId(transactionId)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} processed for booking: {}", transactionId, booking.getId());

        // Notify user
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            notificationService.sendPaymentSuccess(user, saved);
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "bookingId", bookingId));
        return toResponse(payment);
    }

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .transactionId(payment.getTransactionId())
                .build();
    }
}
