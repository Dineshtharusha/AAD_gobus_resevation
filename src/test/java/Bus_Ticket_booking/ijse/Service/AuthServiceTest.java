package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.*;
import Bus_Ticket_booking.ijse.DTO.response.AuthResponse;
import Bus_Ticket_booking.ijse.Service.Serviceimpl.AuthServiceImpl;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private VerificationTokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Role userRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRole = Role.builder().id(1L).name(RoleName.ROLE_USER).build();
        testUser = User.builder()
                .id(10L)
                .username("kasun99")
                .email("kasun@example.com")
                .password("encoded_pass")
                .fullName("Kasun Silva")
                .enabled(false)
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
    }

    @Test
    void testRegister_createsDisabledUserAndSendsEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("kasun99");
        request.setEmail("kasun@example.com");
        request.setPassword("Password@123");
        request.setFullName("Kasun Silva");
        request.setPhone("+94771122334");

        when(userRepository.existsByUsername("kasun99")).thenReturn(false);
        when(userRepository.existsByEmail("kasun@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("kasun@example.com", response.getEmail());
        assertFalse(response.isEmailVerified());
        verify(tokenRepository).save(any(VerificationToken.class));
        verify(emailService).sendVerificationEmail(eq("kasun@example.com"), eq("Kasun Silva"), anyString());
    }

    @Test
    void testVerifyEmail_success() {
        VerifyEmailRequest request = new VerifyEmailRequest("kasun@example.com", "123456");
        VerificationToken token = VerificationToken.builder()
                .id(1L)
                .token("123456")
                .type(VerificationType.EMAIL_VERIFICATION)
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        when(userRepository.findByEmail("kasun@example.com")).thenReturn(Optional.of(testUser));
        when(tokenRepository.findTopByUserAndTokenAndTypeAndUsedFalseOrderByCreatedAtDesc(
                eq(testUser), eq("123456"), eq(VerificationType.EMAIL_VERIFICATION)))
                .thenReturn(Optional.of(token));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("mock-jwt-token");

        AuthResponse response = authService.verifyEmail(request);

        assertNotNull(response);
        assertTrue(response.isEmailVerified());
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertTrue(testUser.isEnabled());
        assertTrue(token.isUsed());
    }

    @Test
    void testVerifyEmail_expiredCodeThrowsException() {
        VerifyEmailRequest request = new VerifyEmailRequest("kasun@example.com", "123456");
        VerificationToken token = VerificationToken.builder()
                .id(1L)
                .token("123456")
                .type(VerificationType.EMAIL_VERIFICATION)
                .user(testUser)
                .expiryDate(LocalDateTime.now().minusMinutes(1)) // expired
                .used(false)
                .build();

        when(userRepository.findByEmail("kasun@example.com")).thenReturn(Optional.of(testUser));
        when(tokenRepository.findTopByUserAndTokenAndTypeAndUsedFalseOrderByCreatedAtDesc(
                eq(testUser), eq("123456"), eq(VerificationType.EMAIL_VERIFICATION)))
                .thenReturn(Optional.of(token));

        assertThrows(BadRequestException.class, () -> authService.verifyEmail(request));
    }

    @Test
    void testForgotPassword_generatesTokenAndSendsEmail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("kasun@example.com");
        when(userRepository.findByEmail("kasun@example.com")).thenReturn(Optional.of(testUser));

        authService.forgotPassword(request);

        verify(tokenRepository).save(any(VerificationToken.class));
        verify(emailService).sendPasswordResetEmail(eq("kasun@example.com"), eq("Kasun Silva"), anyString());
    }

    @Test
    void testResetPassword_success() {
        ResetPasswordRequest request = new ResetPasswordRequest("kasun@example.com", "654321", "NewPassword@123");
        VerificationToken token = VerificationToken.builder()
                .id(2L)
                .token("654321")
                .type(VerificationType.PASSWORD_RESET)
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        when(userRepository.findByEmail("kasun@example.com")).thenReturn(Optional.of(testUser));
        when(tokenRepository.findTopByUserAndTokenAndTypeAndUsedFalseOrderByCreatedAtDesc(
                eq(testUser), eq("654321"), eq(VerificationType.PASSWORD_RESET)))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode("NewPassword@123")).thenReturn("new_encoded_pass");

        authService.resetPassword(request);

        assertEquals("new_encoded_pass", testUser.getPassword());
        assertTrue(token.isUsed());
        verify(userRepository).save(testUser);
    }
}
