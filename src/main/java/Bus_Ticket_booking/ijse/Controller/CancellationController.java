package Bus_Ticket_booking.ijse.Controller;

import Bus_Ticket_booking.ijse.DTO.request.CancellationRequest;
import Bus_Ticket_booking.ijse.DTO.response.ApiResponse;
import Bus_Ticket_booking.ijse.DTO.response.CancellationResponse;
import Bus_Ticket_booking.ijse.Service.CancellationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cancellations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cancellations", description = "Booking cancellation endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CancellationController {

    private final CancellationService cancellationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Request cancellation for a booking (80% refund)")
    public ResponseEntity<ApiResponse<CancellationResponse>> requestCancellation(
            @Valid @RequestBody CancellationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CancellationResponse response = cancellationService.requestCancellation(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Cancellation processed"));
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get cancellation details for a booking")
    public ResponseEntity<ApiResponse<CancellationResponse>> getCancellationByBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success(
                cancellationService.getCancellationByBookingId(bookingId)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all cancellations (ADMIN only)")
    public ResponseEntity<ApiResponse<List<CancellationResponse>>> getAllCancellations() {
        return ResponseEntity.ok(ApiResponse.success(cancellationService.getAllCancellations()));
    }
}
