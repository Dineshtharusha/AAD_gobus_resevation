package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.Cancellation;
import Bus_Ticket_booking.ijse.enumaration.CancellationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CancellationRepository extends JpaRepository<Cancellation, Long> {
    Optional<Cancellation> findByBookingId(Long bookingId);
    List<Cancellation> findByStatus(CancellationStatus status);
}
