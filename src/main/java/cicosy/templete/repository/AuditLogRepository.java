// AuditLogRepository.java
package cicosy.templete.repository;

import cicosy.templete.domain.AuditLog;
import cicosy.templete.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUser(User user);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<AuditLog> findByUserOrderByTimestampDesc(User user);
    List<AuditLog> findTop50ByOrderByTimestampDesc();
}