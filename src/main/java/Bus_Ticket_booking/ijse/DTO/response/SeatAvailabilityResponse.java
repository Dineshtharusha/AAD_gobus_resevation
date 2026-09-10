package Bus_Ticket_booking.ijse.DTO.response;

import Bus_Ticket_booking.ijse.enumaration.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityResponse {
    private Long id;
    private String seatNumber;
    private SeatType seatType;
    private boolean booked;
}
