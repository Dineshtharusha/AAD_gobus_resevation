package Bus_Ticket_booking.ijse.DTO.response;

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
public class ScheduleResponse {
    private Long id;
    private Long routeId;
    private String source;
    private String destination;
    private Long busId;
    private String busNumber;
    private String busName;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal fare;
    private Integer availableSeats;
    private boolean active;
}
