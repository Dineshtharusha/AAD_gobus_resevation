package Bus_Ticket_booking.ijse.Service;

public interface EmailService {
    boolean sendVerificationEmail(String toEmail, String fullName, String verificationCode);
    boolean sendPasswordResetEmail(String toEmail, String fullName, String resetCode);
}
