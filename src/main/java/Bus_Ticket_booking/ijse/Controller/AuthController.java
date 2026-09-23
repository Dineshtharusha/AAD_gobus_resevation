package Bus_Ticket_booking.ijse.Controller;

import Bus_Ticket_booking.ijse.DTO.request.*;
import Bus_Ticket_booking.ijse.DTO.response.ApiResponse;
import Bus_Ticket_booking.ijse.DTO.response.AuthResponse;
import Bus_Ticket_booking.ijse.DTO.response.ForgotPasswordResponse;
import Bus_Ticket_booking.ijse.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Register, login, email verification, and password reset endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account (verification email will be sent)")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request for username: {}, email: {}", request.getUsername(), request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Registration successful! Please check your email for the 6-digit verification code."));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify user email using 6-digit OTP code")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        log.info("Email verification attempt for: {}", request.getEmail());
        AuthResponse response = authService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Email verified successfully! Your account is now active."));
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend email verification code")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        log.info("Resend verification code request for: {}", request.getEmail());
        authService.resendVerification(request);
        return ResponseEntity.ok(ApiResponse.success(null, "A new verification code has been sent to your email."));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset verification code")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for: {}", request.getEmail());
        ForgotPasswordResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(response, response.getMessage()));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using verification code")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Password reset submission for: {}", request.getEmail());
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Your password has been reset successfully. You can now log in."));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for: {}", request.getUsernameOrEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }
}
