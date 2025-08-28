// AuditLogService.java
package cicosy.templete.service;

import cicosy.templete.domain.AuditLog;
import cicosy.templete.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {
    AuditLog logAction(String action, User user, String details);
    List<AuditLog> findByUser(User user);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByDateRange(LocalDateTime start, LocalDateTime end);
    List<AuditLog> findAll();
}