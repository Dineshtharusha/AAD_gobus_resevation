package Bus_Ticket_booking.ijse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stops")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String location;

    @OneToMany(mappedBy = "stop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RouteStop> routeStops = new ArrayList<>();
}
