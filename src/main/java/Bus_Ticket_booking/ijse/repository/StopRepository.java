package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
    Optional<Stop> findByNameIgnoreCase(String name);
}
