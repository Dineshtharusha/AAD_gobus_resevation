package Bus_Ticket_booking.ijse.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String nic;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(length = 15)
    private String phone;

    @OneToOne(mappedBy = "passenger", fetch = FetchType.LAZY)
    private Booking booking;
}
