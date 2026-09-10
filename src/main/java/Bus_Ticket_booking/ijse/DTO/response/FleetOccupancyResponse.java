package Bus_Ticket_booking.ijse.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FleetOccupancyResponse {
    private Long busId;
    private String busNumber;
    private String busName;
    private int totalSeats;
    private int bookedSeats;
    private double occupancyPercentage;
}
