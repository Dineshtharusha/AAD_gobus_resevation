package Bus_Ticket_booking.ijse.DTO.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScheduleRequest {

    @NotNull(message = "Route ID is required")
    private Long routeId;

    @NotNull(message = "Bus ID is required")
    private Long busId;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    @Future(message = "Arrival time must be in the future")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Fare is required")
    @Positive(message = "Fare must be positive")
    private BigDecimal fare;

    @NotNull(message = "Available seats is required")
    @Min(value = 1)
    private Integer availableSeats;
}
