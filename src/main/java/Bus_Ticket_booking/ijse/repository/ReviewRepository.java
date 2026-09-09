package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRouteId(Long routeId);
    List<Review> findByUserId(Long userId);
    Optional<Review> findByUserIdAndRouteId(Long userId, Long routeId);
    boolean existsByUserIdAndRouteId(Long userId, Long routeId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.route.id = :routeId")
    Double findAverageRatingByRouteId(@Param("routeId") Long routeId);
}
