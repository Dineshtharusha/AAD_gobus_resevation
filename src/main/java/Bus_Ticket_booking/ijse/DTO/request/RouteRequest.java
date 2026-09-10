package Bus_Ticket_booking.ijse.DTO.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RouteRequest {

    @NotBlank(message = "Source city is required")
    @Size(max = 100)
    private String source;

    @NotBlank(message = "Destination city is required")
    @Size(max = 100)
    private String destination;

    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private BigDecimal distanceKm;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @NotNull(message = "Base fare is required")
    @Positive(message = "Base fare must be positive")
    private BigDecimal baseFare;
}
