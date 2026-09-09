package Bus_Ticket_booking.ijse.entity;

import Bus_Ticket_booking.ijse.enumaration.SeatType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bus_seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"bus_id", "seat_number"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class BusSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false, length = 20)
    private SeatType seatType;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private boolean available = true;
}
