package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.LoginRequest;
import Bus_Ticket_booking.ijse.DTO.request.RegisterRequest;
import Bus_Ticket_booking.ijse.DTO.response.AuthResponse;
import Bus_Ticket_booking.ijse.entity.Role;
import Bus_Ticket_booking.ijse.entity.User;
import Bus_Ticket_booking.ijse.enumaration.RoleName;
import Bus_Ticket_booking.ijse.exception.BadRequestException;
import Bus_Ticket_booking.ijse.repository.RoleRepository;
import Bus_Ticket_booking.ijse.repository.UserRepository;
import Bus_Ticket_booking.ijse.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' is already registered");
        }

        // Assign default USER role
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new BadRequestException("Default role not found. Please contact administrator."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getUsername());

        String token = jwtTokenProvider.generateToken(savedUser.getUsername());
        List<String> roleNames = savedUser.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .accessToken(token)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .roles(roleNames)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
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
                .build();
    }
}
