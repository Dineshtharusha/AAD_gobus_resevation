package Bus_Ticket_booking.ijse.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "route_stops", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"route_id", "stop_order"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;

    /** Order of the stop along the route (1 = first stop after source) */
    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    /** Estimated arrival time offset in minutes from departure */
    @Column(name = "arrival_offset_minutes")
    private Integer arrivalOffsetMinutes;
}
