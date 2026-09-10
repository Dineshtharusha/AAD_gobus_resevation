package Bus_Ticket_booking.ijse.DTO.response;

import Bus_Ticket_booking.ijse.enumaration.BusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusResponse {
    private Long id;
    private String busNumber;
    private String busName;
    private BusType busType;
    private Integer totalSeats;
    private boolean active;
}
