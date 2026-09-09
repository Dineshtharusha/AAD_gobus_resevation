package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    List<RouteStop> findByRouteIdOrderByStopOrder(Long routeId);
    List<RouteStop> findByStopId(Long stopId);
}
