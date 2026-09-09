package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserIdOrderByTimestampDesc(Long userId);
    List<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId);
    List<AuditLog> findByAction(String action);
}
