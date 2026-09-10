package Bus_Ticket_booking.ijse.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboardResponse {
    private DashboardOverviewResponse overview;
    private List<RevenueTrendResponse> revenueTrends;
    private List<RoutePerformanceResponse> routePerformance;
    private List<FleetOccupancyResponse> fleetOccupancy;
    private List<PaymentMethodStatsResponse> paymentMethods;
    private List<BookingStatusStatsResponse> bookingStatuses;
}
