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
public class RoutePerformanceResponse {
    private Long routeId;
    private String source;
    private String destination;
    private long bookingsCount;
    private BigDecimal totalRevenue;
    private long passengerCount;
    private double averageRating;
}
