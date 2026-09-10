package Bus_Ticket_booking.ijse.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** Public endpoints that require no authentication */
    private static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:8080",
                "http://127.0.0.1:5500",
                "http://127.0.0.1:8080",
                "file://",
                "*"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Disable CSRF – we use stateless JWT
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless session
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                    // Public endpoints
                    .requestMatchers(PUBLIC_URLS).permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/routes/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/schedules/**").permitAll()

                    // Admin-only write operations
                    .requestMatchers(HttpMethod.POST,   "/api/v1/routes/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT,    "/api/v1/routes/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/routes/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST,   "/api/v1/buses/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT,    "/api/v1/buses/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/buses/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST,   "/api/v1/schedules/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT,    "/api/v1/schedules/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/schedules/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/users/**").hasRole("ADMIN")

                    // All other requests require authentication
                    .anyRequest().authenticated()
            )

            // Register JWT filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // Auth provider
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
