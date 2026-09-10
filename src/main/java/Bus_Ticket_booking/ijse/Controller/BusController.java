package Bus_Ticket_booking.ijse.Controller;

import Bus_Ticket_booking.ijse.DTO.request.BusRequest;
import Bus_Ticket_booking.ijse.DTO.response.ApiResponse;
import Bus_Ticket_booking.ijse.DTO.response.BusResponse;
import Bus_Ticket_booking.ijse.Service.BusService;
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
@RequestMapping("/api/v1/buses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Buses", description = "Bus fleet management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class BusController {

    private final BusService busService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get all buses")
    public ResponseEntity<ApiResponse<List<BusResponse>>> getAllBuses() {
        return ResponseEntity.ok(ApiResponse.success(busService.getAllBuses()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get bus by ID")
    public ResponseEntity<ApiResponse<BusResponse>> getBusById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(busService.getBusById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new bus (ADMIN only)")
    public ResponseEntity<ApiResponse<BusResponse>> createBus(@Valid @RequestBody BusRequest request) {
        BusResponse response = busService.createBus(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Bus created with auto-generated seats"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update bus details (ADMIN only)")
    public ResponseEntity<ApiResponse<BusResponse>> updateBus(
            @PathVariable Long id, @Valid @RequestBody BusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(busService.updateBus(id, request), "Bus updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a bus (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Bus deactivated"));
    }
}
