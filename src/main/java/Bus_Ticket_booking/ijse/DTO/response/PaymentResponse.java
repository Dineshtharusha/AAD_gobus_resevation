package Bus_Ticket_booking.ijse.DTO.response;

import Bus_Ticket_booking.ijse.enumaration.PaymentMethod;
import Bus_Ticket_booking.ijse.enumaration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
    private PaymentMethod method;
    private String transactionId;
}
