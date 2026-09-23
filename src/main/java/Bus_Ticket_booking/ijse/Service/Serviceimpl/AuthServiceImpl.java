package Bus_Ticket_booking.ijse.Service.Serviceimpl;

import Bus_Ticket_booking.ijse.DTO.request.*;
import Bus_Ticket_booking.ijse.DTO.response.AuthResponse;
import Bus_Ticket_booking.ijse.DTO.response.ForgotPasswordResponse;
import Bus_Ticket_booking.ijse.Service.AuthService;
import Bus_Ticket_booking.ijse.Service.EmailService;
import Bus_Ticket_booking.ijse.entity.Role;
import Bus_Ticket_booking.ijse.entity.User;
import Bus_Ticket_booking.ijse.entity.VerificationToken;
import Bus_Ticket_booking.ijse.enumaration.RoleName;
import Bus_Ticket_booking.ijse.enumaration.VerificationType;
import Bus_Ticket_booking.ijse.exception.BadRequestException;
import Bus_Ticket_booking.ijse.repository.RoleRepository;
import Bus_Ticket_booking.ijse.repository.UserRepository;
import Bus_Ticket_booking.ijse.repository.VerificationTokenRepository;
import Bus_Ticket_booking.ijse.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username '" + username + "' is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email '" + email + "' is already registered");
        }

        // Assign default USER role
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new BadRequestException("Default role not found. Please contact administrator."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        // Account requires email verification before activation
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone())
                .enabled(false)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered (pending email verification): {}", savedUser.getUsername());

        // Generate and save 6-digit OTP verification token
        String otp = generateOtp();
        createAndSaveToken(savedUser, otp, VerificationType.EMAIL_VERIFICATION);

        // Send real email verification
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getFullName(), otp);

        List<String> roleNames = savedUser.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .roles(roleNames)
                .emailVerified(false)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse verifyEmail(VerifyEmailRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String code = request.getCode().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No user found with email: " + email));

        if (user.isEnabled()) {
            // Already verified
            String token = jwtTokenProvider.generateToken(user.getUsername());
            List<String> roles = user.getRoles().stream()
                    .map(r -> r.getName().name())
                    .collect(Collectors.toList());

            return AuthResponse.builder()
                    .accessToken(token)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(roles)
                    .emailVerified(true)
                    .build();
        }

        VerificationToken verificationToken = tokenRepository
                .findTopByUserAndTokenAndTypeAndUsedFalseOrderByCreatedAtDesc(
                        user, code, VerificationType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification code"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification code has expired. Please request a new code.");
        }

        // Mark token used and enable user
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        user.setEnabled(true);
        User verifiedUser = userRepository.save(user);
        log.info("User email verified successfully: {}", verifiedUser.getUsername());

        String token = jwtTokenProvider.generateToken(verifiedUser.getUsername());
        List<String> roles = verifiedUser.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .accessToken(token)
                .userId(verifiedUser.getId())
                .username(verifiedUser.getUsername())
                .email(verifiedUser.getEmail())
                .roles(roles)
                .emailVerified(true)
                .build();
    }

    @Override
    @Transactional
    public void resendVerification(ResendVerificationRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No user found with email: " + email));

        if (user.isEnabled()) {
            throw new BadRequestException("Account is already verified. Please proceed to sign in.");
        }

        // Invalidate older unused tokens
        tokenRepository.findTopByUserAndTypeAndUsedFalseOrderByCreatedAtDesc(user, VerificationType.EMAIL_VERIFICATION)
                .ifPresent(t -> {
                    t.setUsed(true);
                    tokenRepository.save(t);
                });

        String otp = generateOtp();
        createAndSaveToken(user, otp, VerificationType.EMAIL_VERIFICATION);
        emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), otp);
        log.info("Resent verification code to: {}", email);
    }

    @Override
    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No account registered with email: " + email));

        // Invalidate older unused reset tokens
        tokenRepository.findTopByUserAndTypeAndUsedFalseOrderByCreatedAtDesc(user, VerificationType.PASSWORD_RESET)
                .ifPresent(t -> {
                    t.setUsed(true);
                    tokenRepository.save(t);
                });

        String otp = generateOtp();
        createAndSaveToken(user, otp, VerificationType.PASSWORD_RESET);
        boolean emailSent = emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), otp);
        log.info("Sent password reset code to: {} (SMTP success: {})", email, emailSent);

        String message = emailSent
                ? "Password reset code sent to your email."
                : "Failed to send reset email. Please verify your email configuration.";

        return ForgotPasswordResponse.builder()
                .email(email)
                .emailSent(emailSent)
                .resetCode(null)
                .message(message)
                .build();
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String code = request.getCode().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No account registered with email: " + email));

        VerificationToken verificationToken = tokenRepository
                .findTopByUserAndTokenAndTypeAndUsedFalseOrderByCreatedAtDesc(
                        user, code, VerificationType.PASSWORD_RESET)
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset code"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Password reset code has expired. Please request a new one.");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        log.info("Password successfully reset for user: {}", user.getUsername());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String loginIdentifier = request.getUsernameOrEmail().trim();

        // Check if user exists and is disabled (e.g. registered before email fix)
        User existingUser = userRepository.findByUsername(loginIdentifier)
                .or(() -> userRepository.findByEmail(loginIdentifier))
                .or(() -> userRepository.findByEmail(loginIdentifier.toLowerCase()))
                .orElse(null);

        if (existingUser != null && !existingUser.isEnabled()) {
            log.warn("Login rejected – email not verified: {}", loginIdentifier);
            throw new BadRequestException("Your email is not verified yet. Please check your email for the verification code.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginIdentifier,
                            request.getPassword())
            );

            String token = jwtTokenProvider.generateToken(authentication);
            User user = (User) authentication.getPrincipal();

            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            log.info("User logged in: {}", user.getUsername());

            return AuthResponse.builder()
                    .accessToken(token)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(roles)
                    .emailVerified(true)
                    .build();

        } catch (DisabledException ex) {
            log.warn("Login rejected – user account disabled: {}", request.getUsernameOrEmail());
            throw new BadRequestException("Your account is disabled. Please contact customer support.");
        }
    }

    private void createAndSaveToken(User user, String code, VerificationType type) {
        VerificationToken token = VerificationToken.builder()
                .token(code)
                .type(type)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();
        tokenRepository.save(token);
    }

    private String generateOtp() {
        int code = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}
