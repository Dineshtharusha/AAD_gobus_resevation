package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.Role;
import Bus_Ticket_booking.ijse.enumaration.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
