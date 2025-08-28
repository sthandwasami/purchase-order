// AuditLogServiceImpl.java
package cicosy.templete.service.impl;

import cicosy.templete.domain.AuditLog;
import cicosy.templete.domain.User;
import cicosy.templete.repository.AuditLogRepository;
import cicosy.templete.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public AuditLog logAction(String action, User user, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setUser(user);
        auditLog.setDetails(details);
        auditLog.setTimestamp(LocalDateTime.now());
        return auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLog> findByUser(User user) {
        return auditLogRepository.findByUser(user);
    }

    @Override
    public List<AuditLog> findByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    @Override
    public List<AuditLog> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }

    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }
}