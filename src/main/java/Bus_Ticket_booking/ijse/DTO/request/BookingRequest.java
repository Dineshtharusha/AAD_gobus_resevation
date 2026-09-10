package Bus_Ticket_booking.ijse.DTO.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    @NotNull(message = "Passenger details are required")
    @Valid
    private PassengerRequest passenger;

    @NotNull(message = "Seat selection is required")
    @Size(min = 1, message = "At least one seat must be selected")
    private List<Long> seatIds;
}
