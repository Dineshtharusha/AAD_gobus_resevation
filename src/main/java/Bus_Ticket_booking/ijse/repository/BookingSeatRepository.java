package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    List<BookingSeat> findByBookingId(Long bookingId);
    boolean existsByBusSeatIdAndBookingScheduleId(Long busSeatId, Long scheduleId);
    List<BookingSeat> findByBookingScheduleId(Long scheduleId);
}
