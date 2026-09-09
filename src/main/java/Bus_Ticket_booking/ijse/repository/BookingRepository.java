package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.Booking;
import Bus_Ticket_booking.ijse.enumaration.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByScheduleId(Long scheduleId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
}
