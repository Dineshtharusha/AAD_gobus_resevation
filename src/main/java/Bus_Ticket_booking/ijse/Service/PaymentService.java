package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.PaymentRequest;
import Bus_Ticket_booking.ijse.DTO.response.PaymentResponse;
import Bus_Ticket_booking.ijse.entity.Payment;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request, String username);
    PaymentResponse getPaymentByBookingId(Long bookingId);
    PaymentResponse toResponse(Payment payment);
}
