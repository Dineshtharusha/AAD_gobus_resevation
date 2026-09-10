package Bus_Ticket_booking.ijse.Controller;

import Bus_Ticket_booking.ijse.DTO.response.*;
import Bus_Ticket_booking.ijse.Service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "Real-time analytics and visual dashboard reporting")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get high-level dashboard KPIs and totals (ADMIN only)")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getOverview() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getOverview()));
    }

    @GetMapping("/revenue-trends")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get daily revenue and booking trends (ADMIN only)")
    public ResponseEntity<ApiResponse<List<RevenueTrendResponse>>> getRevenueTrends(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getRevenueTrends(days)));
    }

    @GetMapping("/route-performance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get performance and popularity metrics for all routes (ADMIN only)")
    public ResponseEntity<ApiResponse<List<RoutePerformanceResponse>>> getRoutePerformance() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getRoutePerformance()));
    }

    @GetMapping("/fleet-occupancy")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get seat occupancy and fleet utilization (ADMIN only)")
    public ResponseEntity<ApiResponse<List<FleetOccupancyResponse>>> getFleetOccupancy() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getFleetOccupancy()));
    }

    @GetMapping("/payment-methods")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get payment method breakdown (ADMIN only)")
    public ResponseEntity<ApiResponse<List<PaymentMethodStatsResponse>>> getPaymentMethods() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getPaymentMethodStats()));
    }

    @GetMapping("/booking-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get booking status distribution (ADMIN only)")
    public ResponseEntity<ApiResponse<List<BookingStatusStatsResponse>>> getBookingStatuses() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getBookingStatusStats()));
    }

    @GetMapping("/full")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get complete analytics payload in a single call (ADMIN only)")
    public ResponseEntity<ApiResponse<AnalyticsDashboardResponse>> getFullAnalytics(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getFullAnalytics(days)));
    }
}
