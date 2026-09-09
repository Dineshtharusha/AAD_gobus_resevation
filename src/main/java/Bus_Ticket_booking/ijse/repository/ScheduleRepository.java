package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByRouteId(Long routeId);

    List<Schedule> findByBusId(Long busId);

    @Query("SELECT s FROM Schedule s WHERE s.route.id = :routeId AND s.active = true " +
           "AND s.departureTime BETWEEN :from AND :to ORDER BY s.departureTime")
    List<Schedule> findAvailableSchedules(
            @Param("routeId") Long routeId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("SELECT s FROM Schedule s WHERE s.active = true AND s.availableSeats > 0 ORDER BY s.departureTime")
    List<Schedule> findAllActiveWithSeats();
}
