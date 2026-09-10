package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.response.NotificationResponse;
import Bus_Ticket_booking.ijse.entity.Booking;
import Bus_Ticket_booking.ijse.entity.Cancellation;
import Bus_Ticket_booking.ijse.entity.Notification;
import Bus_Ticket_booking.ijse.entity.Payment;
import Bus_Ticket_booking.ijse.entity.User;

import java.util.List;

public interface NotificationService {
    void sendBookingConfirmation(User user, Booking booking);
    void sendPaymentSuccess(User user, Payment payment);
    void sendCancellationConfirmed(User user, Cancellation cancellation);
    List<NotificationResponse> getMyNotifications(String username);
    long getUnreadCount(String username);
    void markAllRead(String username);
    void markRead(Long notificationId);
    NotificationResponse toResponse(Notification n);
}
