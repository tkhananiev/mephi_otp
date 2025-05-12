package otp.sf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import otp.sf.domain.model.OtpCode;
import otp.sf.domain.model.Status;
import otp.sf.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий OTP-кодов
 */
@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByOperationId(Long id);
    Optional<OtpCode> findByOperationIdAndUser(Long id, User user);
    Optional<OtpCode> findByOperationIdAndStatusAndUser(Long id, Status status, User user);
    List<OtpCode> findByStatus(Status status);
    List<OtpCode> findByStatusAndUser(Status status, User user);
    boolean existsByOperationIdAndStatusAndUser(Long id, Status status, User user);
    boolean existsByCodeAndStatusAndUser(String code, Status status, User user);
}
