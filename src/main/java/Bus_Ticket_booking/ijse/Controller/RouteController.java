package Bus_Ticket_booking.ijse.Controller;

import Bus_Ticket_booking.ijse.DTO.request.RouteRequest;
import Bus_Ticket_booking.ijse.DTO.response.ApiResponse;
import Bus_Ticket_booking.ijse.DTO.response.RouteResponse;
import Bus_Ticket_booking.ijse.Service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Routes", description = "Bus route management endpoints")
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    @Operation(summary = "Get all active routes (public)")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getAllRoutes() {
        return ResponseEntity.ok(ApiResponse.success(routeService.getAllActiveRoutes()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID (public)")
    public ResponseEntity<ApiResponse<RouteResponse>> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(routeService.getRouteById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search routes by source and destination (public)")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> searchRoutes(
            @RequestParam String source,
            @RequestParam String destination) {
        return ResponseEntity.ok(ApiResponse.success(routeService.searchRoutes(source, destination)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new route (ADMIN only)")
    public ResponseEntity<ApiResponse<RouteResponse>> createRoute(@Valid @RequestBody RouteRequest request) {
        RouteResponse response = routeService.createRoute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Route created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a route (ADMIN only)")
    public ResponseEntity<ApiResponse<RouteResponse>> updateRoute(
            @PathVariable Long id, @Valid @RequestBody RouteRequest request) {
        return ResponseEntity.ok(ApiResponse.success(routeService.updateRoute(id, request), "Route updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Soft-delete a route (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Route deleted"));
    }
}
