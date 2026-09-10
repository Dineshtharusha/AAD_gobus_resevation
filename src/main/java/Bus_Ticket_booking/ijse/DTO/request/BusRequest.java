package Bus_Ticket_booking.ijse.DTO.request;

import Bus_Ticket_booking.ijse.enumaration.BusType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BusRequest {

    @NotBlank(message = "Bus number is required")
    @Size(max = 20)
    private String busNumber;

    @NotBlank(message = "Bus name is required")
    @Size(max = 100)
    private String busName;

    @NotNull(message = "Bus type is required")
    private BusType busType;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Bus must have at least 1 seat")
    @Max(value = 100, message = "Bus cannot have more than 100 seats")
    private Integer totalSeats;
}
