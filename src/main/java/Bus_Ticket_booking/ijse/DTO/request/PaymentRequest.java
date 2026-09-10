package Bus_Ticket_booking.ijse.DTO.request;

import Bus_Ticket_booking.ijse.enumaration.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;
}
