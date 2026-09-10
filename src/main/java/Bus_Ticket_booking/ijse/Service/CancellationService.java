package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.CancellationRequest;
import Bus_Ticket_booking.ijse.DTO.response.CancellationResponse;
import Bus_Ticket_booking.ijse.entity.Cancellation;

import java.util.List;

public interface CancellationService {
    CancellationResponse requestCancellation(CancellationRequest request, String username);
    List<CancellationResponse> getAllCancellations();
    CancellationResponse getCancellationByBookingId(Long bookingId);
    CancellationResponse toResponse(Cancellation c);
}
