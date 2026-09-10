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
public class DashboardOverviewResponse {
    private BigDecimal totalRevenue;
    private long totalBookings;
    private long totalUsers;
    private long totalBuses;
    private long totalRoutes;
    private long totalSchedules;
    private long activeSchedules;
    private long totalCancellations;
    private double occupancyRate;
    private double avgRouteRating;
}
