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
public class RouteResponse {
    private Long id;
    private String source;
    private String destination;
    private BigDecimal distanceKm;
    private Integer durationMinutes;
    private BigDecimal baseFare;
    private boolean active;
    private Double averageRating;
}
