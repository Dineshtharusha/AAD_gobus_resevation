package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.response.*;

import java.util.List;

public interface AnalyticsService {
    DashboardOverviewResponse getOverview();
    List<RevenueTrendResponse> getRevenueTrends(int days);
    List<RoutePerformanceResponse> getRoutePerformance();
    List<FleetOccupancyResponse> getFleetOccupancy();
    List<PaymentMethodStatsResponse> getPaymentMethodStats();
    List<BookingStatusStatsResponse> getBookingStatusStats();
    AnalyticsDashboardResponse getFullAnalytics(int days);
}
