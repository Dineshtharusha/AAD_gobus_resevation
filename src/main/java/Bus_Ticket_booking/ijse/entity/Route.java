package Bus_Ticket_booking.ijse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false, length = 100)
    private String destination;

    /** Distance in kilometres */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal distanceKm;

    /** Estimated journey duration in minutes */
    @Column(nullable = false)
    private Integer durationMinutes;

    /** Base fare for this route */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal baseFare;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RouteStop> routeStops = new ArrayList<>();

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();
}
