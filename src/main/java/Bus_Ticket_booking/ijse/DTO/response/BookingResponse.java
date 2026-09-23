package Bus_Ticket_booking.ijse.DTO.response;

import Bus_Ticket_booking.ijse.enumaration.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingReference;
    private Long userId;
    private String username;
    private Long scheduleId;
    private Long routeId;
    private String source;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String busName;
    private String busNumber;
    private String passengerName;
    private String passengerNic;
    private String passengerPhone;
    private String passengerEmail;
    private List<String> seatNumbers;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private BigDecimal totalAmount;
}
