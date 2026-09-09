package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.response.NotificationResponse;
import Bus_Ticket_booking.ijse.entity.Booking;
import Bus_Ticket_booking.ijse.entity.Cancellation;
import Bus_Ticket_booking.ijse.entity.Notification;
import Bus_Ticket_booking.ijse.entity.Payment;
import Bus_Ticket_booking.ijse.entity.User;
import Bus_Ticket_booking.ijse.enumaration.NotificationType;
import Bus_Ticket_booking.ijse.exception.ResourceNotFoundException;
import Bus_Ticket_booking.ijse.repository.NotificationRepository;
import Bus_Ticket_booking.ijse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // ===== Internal notification senders =====

    public void sendBookingConfirmation(User user, Booking booking) {
        String message = String.format(
                "Booking #%d confirmed! %s → %s on %s. Amount: %.2f",
                booking.getId(),
                booking.getSchedule().getRoute().getSource(),
                booking.getSchedule().getRoute().getDestination(),
                booking.getSchedule().getDepartureTime(),
                booking.getTotalAmount());
        save(user, message, NotificationType.BOOKING_CONFIRMED);
    }

    public void sendPaymentSuccess(User user, Payment payment) {
        String message = String.format(
                "Payment of %.2f received. Transaction ID: %s",
                payment.getAmount(), payment.getTransactionId());
        save(user, message, NotificationType.PAYMENT_SUCCESS);
    }

    public void sendCancellationConfirmed(User user, Cancellation cancellation) {
        String message = String.format(
                "Booking #%d cancelled. Refund of %.2f will be processed.",
                cancellation.getBooking().getId(), cancellation.getRefundAmount());
        save(user, message, NotificationType.BOOKING_CANCELLED);
    }

    private void save(User user, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .build();
        notificationRepository.save(notification);
        log.debug("Notification sent to {}: {}", user.getUsername(), message);
    }

    // ===== API methods =====

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return notificationRepository.countByUserIdAndReadFalse(user.getId());
    }

    @Transactional
    public void markAllRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(user.getId());
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
        log.debug("Marked {} notifications as read for {}", unread.size(), username);
    }

    @Transactional
    public void markRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
