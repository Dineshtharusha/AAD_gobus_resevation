package Bus_Ticket_booking.ijse.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking_seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"booking_id", "bus_seat_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_seat_id", nullable = false)
    private BusSeat busSeat;
}
