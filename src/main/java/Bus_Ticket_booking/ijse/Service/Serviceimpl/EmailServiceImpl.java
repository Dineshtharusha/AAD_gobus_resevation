package Bus_Ticket_booking.ijse.Service.Serviceimpl;

import Bus_Ticket_booking.ijse.Service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@gobus.lk}")
    private String fromEmail;

    @Override
    public boolean sendVerificationEmail(String toEmail, String fullName, String verificationCode) {
        String subject = "Verify your GoBus account – " + verificationCode;
        String htmlContent = buildVerificationEmailTemplate(fullName, verificationCode);

        return sendHtmlEmail(toEmail, subject, htmlContent, "Email Verification", verificationCode);
    }

    @Override
    public boolean sendPasswordResetEmail(String toEmail, String fullName, String resetCode) {
        String subject = "Reset your GoBus password – " + resetCode;
        String htmlContent = buildPasswordResetEmailTemplate(fullName, resetCode);

        return sendHtmlEmail(toEmail, subject, htmlContent, "Password Reset", resetCode);
    }

    private boolean sendHtmlEmail(String toEmail, String subject, String htmlContent, String type, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "GoBus Sri Lanka");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Sent {} email successfully to {}", type, toEmail);
            return true;
        } catch (Exception ex) {
            log.error("Failed to send email to {}: {}", toEmail, ex.getMessage(), ex);
            log.warn("[EMAIL FALLBACK] Could not send {} email to {} via SMTP ({}: {}).",
                    type, toEmail, ex.getClass().getSimpleName(), ex.getMessage());
            log.info("===============================================================");
            log.info("[DEV CONSOLE OTP] {} code for {}: >>> {} <<<", type.toUpperCase(), toEmail, code);
            log.info("===============================================================");
            return false;
        }
    }

    private String buildVerificationEmailTemplate(String fullName, String code) {
        String greeting = (fullName != null && !fullName.isBlank()) ? fullName : "Traveler";
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Verify your GoBus account</title>"
                + "</head>"
                + "<body style='margin:0;padding:0;background-color:#0f172a;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",Roboto,Helvetica,Arial,sans-serif;'>"
                + "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='table-layout:fixed;'>"
                + "<tr><td align='center' style='padding:40px 16px;'>"
                + "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='max-width:540px;background:#ffffff;border-radius:18px;overflow:hidden;box-shadow:0 20px 40px rgba(0,0,0,0.25);'>"
                // Header
                + "<tr><td style='background:linear-gradient(135deg, #4f46e5 0%, #3730a3 100%);padding:36px 32px;text-align:center;'>"
                + "<h1 style='margin:0;color:#ffffff;font-size:28px;font-weight:800;letter-spacing:-0.5px;'>🚌 Go<span style='color:#ff5757;'>Bus</span></h1>"
                + "<p style='margin:8px 0 0;color:#e0e7ff;font-size:14px;letter-spacing:0.5px;'>SRI LANKA INTERCITY BUS BOOKINGS</p>"
                + "</td></tr>"
                // Body
                + "<tr><td style='padding:36px 32px;color:#1e293b;'>"
                + "<h2 style='margin:0 0 12px;font-size:22px;color:#0f172a;'>Verify your email address</h2>"
                + "<p style='margin:0 0 20px;font-size:15px;line-height:1.6;color:#475569;'>Hello <strong>" + escape(greeting) + "</strong>,</p>"
                + "<p style='margin:0 0 24px;font-size:15px;line-height:1.6;color:#475569;'>Thank you for registering with GoBus! Please enter the 6-digit verification code below to activate your account and start booking trips across Sri Lanka:</p>"
                // OTP Code Box
                + "<div style='background:#f1f5f9;border:2px dashed #cbd5e1;border-radius:14px;padding:24px;text-align:center;margin-bottom:24px;'>"
                + "<span style='font-size:13px;font-weight:600;letter-spacing:1px;color:#64748b;display:block;margin-bottom:8px;text-transform:uppercase;'>Verification Code</span>"
                + "<span style='font-size:36px;font-weight:800;letter-spacing:8px;color:#4f46e5;font-family:monospace;display:inline-block;'>" + code + "</span>"
                + "</div>"
                + "<p style='margin:0 0 10px;font-size:13px;color:#64748b;line-height:1.5;'>⏱ This code is valid for <strong>15 minutes</strong>. If you did not sign up for GoBus, please ignore this email.</p>"
                + "</td></tr>"
                // Footer
                + "<tr><td style='background:#f8fafc;padding:20px 32px;border-top:1px solid #e2e8f0;text-align:center;color:#94a3b8;font-size:12px;line-height:1.5;'>"
                + "GoBus Online Passenger Transportation System &bull; Colombo, Sri Lanka<br>&copy; 2026 GoBus. All rights reserved."
                + "</td></tr>"
                + "</table>"
                + "</td></tr>"
                + "</table>"
                + "</body></html>";
    }

    private String buildPasswordResetEmailTemplate(String fullName, String code) {
        String greeting = (fullName != null && !fullName.isBlank()) ? fullName : "Traveler";
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Reset your GoBus password</title>"
                + "</head>"
                + "<body style='margin:0;padding:0;background-color:#0f172a;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",Roboto,Helvetica,Arial,sans-serif;'>"
                + "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='table-layout:fixed;'>"
                + "<tr><td align='center' style='padding:40px 16px;'>"
                + "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='max-width:540px;background:#ffffff;border-radius:18px;overflow:hidden;box-shadow:0 20px 40px rgba(0,0,0,0.25);'>"
                // Header
                + "<tr><td style='background:linear-gradient(135deg, #ff5757 0%, #dc2626 100%);padding:36px 32px;text-align:center;'>"
                + "<h1 style='margin:0;color:#ffffff;font-size:28px;font-weight:800;letter-spacing:-0.5px;'>🚌 Go<span style='color:#fde047;'>Bus</span></h1>"
                + "<p style='margin:8px 0 0;color:#fee2e2;font-size:14px;letter-spacing:0.5px;'>PASSWORD RESET REQUEST</p>"
                + "</td></tr>"
                // Body
                + "<tr><td style='padding:36px 32px;color:#1e293b;'>"
                + "<h2 style='margin:0 0 12px;font-size:22px;color:#0f172a;'>Password Reset Code</h2>"
                + "<p style='margin:0 0 20px;font-size:15px;line-height:1.6;color:#475569;'>Hello <strong>" + escape(greeting) + "</strong>,</p>"
                + "<p style='margin:0 0 24px;font-size:15px;line-height:1.6;color:#475569;'>We received a request to reset your password for your GoBus account. Use the 6-digit code below to set a new password:</p>"
                // OTP Code Box
                + "<div style='background:#fef2f2;border:2px dashed #fca5a5;border-radius:14px;padding:24px;text-align:center;margin-bottom:24px;'>"
                + "<span style='font-size:13px;font-weight:600;letter-spacing:1px;color:#b91c1c;display:block;margin-bottom:8px;text-transform:uppercase;'>Password Reset Code</span>"
                + "<span style='font-size:36px;font-weight:800;letter-spacing:8px;color:#dc2626;font-family:monospace;display:inline-block;'>" + code + "</span>"
                + "</div>"
                + "<p style='margin:0 0 10px;font-size:13px;color:#64748b;line-height:1.5;'>⏱ This code expires in <strong>15 minutes</strong>. If you did not request a password reset, you can safely ignore this email; your password will remain unchanged.</p>"
                + "</td></tr>"
                // Footer
                + "<tr><td style='background:#f8fafc;padding:20px 32px;border-top:1px solid #e2e8f0;text-align:center;color:#94a3b8;font-size:12px;line-height:1.5;'>"
                + "GoBus Online Passenger Transportation System &bull; Colombo, Sri Lanka<br>&copy; 2026 GoBus. All rights reserved."
                + "</td></tr>"
                + "</table>"
                + "</td></tr>"
                + "</table>"
                + "</body></html>";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
