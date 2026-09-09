package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.BusSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusSeatRepository extends JpaRepository<BusSeat, Long> {
    List<BusSeat> findByBusId(Long busId);
    List<BusSeat> findByBusIdAndAvailableTrue(Long busId);
    long countByBusIdAndAvailableTrue(Long busId);
}
