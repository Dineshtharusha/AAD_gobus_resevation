package Bus_Ticket_booking.ijse.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodStatsResponse {
    private String method;
    private long count;
    private BigDecimal totalAmount;
    private double percentage;
}
