package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByActiveTrue();

    List<Route> findBySourceIgnoreCaseAndDestinationIgnoreCase(String source, String destination);

    @Query("SELECT r FROM Route r WHERE r.active = true AND " +
           "(LOWER(r.source) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.destination) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Route> searchRoutes(@Param("keyword") String keyword);
}
