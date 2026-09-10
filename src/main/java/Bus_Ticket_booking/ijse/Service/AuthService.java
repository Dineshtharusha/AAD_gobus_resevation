package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.LoginRequest;
import Bus_Ticket_booking.ijse.DTO.request.RegisterRequest;
import Bus_Ticket_booking.ijse.DTO.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
