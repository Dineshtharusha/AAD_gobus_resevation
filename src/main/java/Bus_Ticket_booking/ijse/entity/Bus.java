package Bus_Ticket_booking.ijse.entity;

import Bus_Ticket_booking.ijse.enumaration.BusType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bus_number", unique = true, nullable = false, length = 20)
    private String busNumber;

    @Column(name = "bus_name", nullable = false, length = 100)
    private String busName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusType busType;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BusSeat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();
}
