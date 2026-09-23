package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.Booking;
import Bus_Ticket_booking.ijse.enumaration.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByScheduleId(Long scheduleId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    List<Booking> findByScheduleBusOwnerUsername(String username);
    List<Booking> findByScheduleBusId(Long busId);

    @Query("SELECT b FROM Booking b WHERE (b.schedule.bus.owner.username = :username OR b.schedule.bus.owner IS NULL) ORDER BY b.schedule.departureTime DESC")
    List<Booking> findBookingsForOwnerOrUnassigned(@Param("username") String username);
}
