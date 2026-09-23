package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.Bus;
import Bus_Ticket_booking.ijse.enumaration.BusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByBusNumber(String busNumber);
    List<Bus> findByActiveTrue();
    List<Bus> findByBusType(BusType busType);
    boolean existsByBusNumber(String busNumber);
    List<Bus> findByOwnerUsername(String username);
    List<Bus> findByOwnerUsernameAndActiveTrue(String username);
}
