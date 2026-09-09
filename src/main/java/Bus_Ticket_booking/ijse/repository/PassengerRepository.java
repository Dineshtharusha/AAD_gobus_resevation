package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByNic(String nic);
}
