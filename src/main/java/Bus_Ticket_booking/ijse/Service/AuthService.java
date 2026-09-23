package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.ForgotPasswordRequest;
import Bus_Ticket_booking.ijse.DTO.request.LoginRequest;
import Bus_Ticket_booking.ijse.DTO.request.RegisterRequest;
import Bus_Ticket_booking.ijse.DTO.request.ResendVerificationRequest;
import Bus_Ticket_booking.ijse.DTO.request.ResetPasswordRequest;
import Bus_Ticket_booking.ijse.DTO.request.VerifyEmailRequest;
import Bus_Ticket_booking.ijse.DTO.response.AuthResponse;
import Bus_Ticket_booking.ijse.DTO.response.ForgotPasswordResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse verifyEmail(VerifyEmailRequest request);
    void resendVerification(ResendVerificationRequest request);
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
