package Bus_Ticket_booking.ijse.repository;

import Bus_Ticket_booking.ijse.entity.User;
import Bus_Ticket_booking.ijse.entity.VerificationToken;
import Bus_Ticket_booking.ijse.enumaration.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findTopByUserAndTokenAndTypeAndUsedFalseOrderByCreatedAtDesc(
            User user, String token, VerificationType type);

    Optional<VerificationToken> findTopByUserAndTypeAndUsedFalseOrderByCreatedAtDesc(
            User user, VerificationType type);

    Optional<VerificationToken> findByTokenAndTypeAndUsedFalse(
            String token, VerificationType type);

    void deleteByUserAndType(User user, VerificationType type);
}
