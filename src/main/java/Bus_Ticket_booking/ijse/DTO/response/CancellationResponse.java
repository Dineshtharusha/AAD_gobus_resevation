package Bus_Ticket_booking.ijse.DTO.response;

import Bus_Ticket_booking.ijse.enumaration.CancellationStatus;
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
public class CancellationResponse {
    private Long id;
    private Long bookingId;
    private LocalDateTime cancellationDate;
    private String reason;
    private BigDecimal refundAmount;
    private CancellationStatus status;
}
