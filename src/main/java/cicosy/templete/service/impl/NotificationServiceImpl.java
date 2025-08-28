package cicosy.templete.service.impl;

import cicosy.templete.domain.AuditLog;
import cicosy.templete.domain.PurchaseOrder;
import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;
import cicosy.templete.repository.UserRepository;
import cicosy.templete.service.NotificationService;
import cicosy.templete.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuditLogService auditLogService;

    // Requisition-related notifications
    @Override
    public void notifyAdminOfNewRequisition(Requisition requisition) {
        String message = String.format(
            "New requisition #%d submitted by %s from %s department. Priority: %s. Items: %s",
            requisition.getId(),
            requisition.getUser().getUsername(),
            requisition.getDepartment().getName(),
            requisition.getPriority(),
            getItemsSummary(requisition)
        );
        
        logger.info("ADMIN NOTIFICATION: {}", message);
        notifyUsersByRole(User.Role.ADMIN, "New Requisition Submitted", message);
        
        // Create audit log
        auditLogService.logAction("REQUISITION_SUBMITTED", requisition.getUser(), 
            "Requisition #" + requisition.getId() + " submitted for admin approval");
    }

    @Override
    public void notifyUserOfRequisitionApproval(Requisition requisition) {
        String message = String.format(
            "Your requisition #%d has been approved by admin. Status: %s. Items: %s",
            requisition.getId(),
            requisition.getStatus(),
            getItemsSummary(requisition)
        );
        
        logger.info("USER NOTIFICATION: Notifying {} that requisition {} has been approved",
            requisition.getUser().getUsername(), requisition.getId());
        
        notifySpecificUser(requisition.getUser(), "Requisition Approved", message);
        
        auditLogService.logAction("REQUISITION_APPROVED", requisition.getUser(),
            "Requisition #" + requisition.getId() + " approved by admin");
    }

    @Override
    public void notifyUserOfRequisitionRejection(Requisition requisition) {
        String message = String.format(
            "Your requisition #%d has been rejected. Reason: %s. Items: %s",
            requisition.getId(),
            requisition.getReasonForRejection() != null ? requisition.getReasonForRejection() : "No reason provided",
            getItemsSummary(requisition)
        );
        
        logger.warn("USER NOTIFICATION: Notifying {} that requisition {} has been rejected. Reason: {}",
            requisition.getUser().getUsername(), 
            requisition.getId(), 
            requisition.getReasonForRejection());
        
        notifySpecificUser(requisition.getUser(), "Requisition Rejected", message);
        
        auditLogService.logAction("REQUISITION_REJECTED", requisition.getUser(),
            "Requisition #" + requisition.getId() + " rejected: " + requisition.getReasonForRejection());
    }

    @Override
    public void notifyHodOfDepartmentRequisition(Requisition requisition) {
        if (requisition.getDepartment() != null && requisition.getDepartment().getHod() != null) {
            String message = String.format(
                "New requisition #%d from your department (%s) by %s. Priority: %s. Items: %s",
                requisition.getId(),
                requisition.getDepartment().getName(),
                requisition.getUser().getUsername(),
                requisition.getPriority(),
                getItemsSummary(requisition)
            );
            
            logger.info("HOD NOTIFICATION: Notifying HOD {} of new department requisition {}",
                requisition.getDepartment().getHod().getUsername(), requisition.getId());
            
            notifySpecificUser(requisition.getDepartment().getHod(), "New Department Requisition", message);
        }
    }

    @Override
    public void notifyUsersOfRequisitionConsolidation(List<Requisition> consolidatedRequisitions) {
        if (consolidatedRequisitions == null || consolidatedRequisitions.isEmpty()) return;
        
        String message = String.format(
            "Your requisitions have been consolidated: %s. Total items consolidated: %d",
            consolidatedRequisitions.stream()
                .map(r -> "#" + r.getId())
                .collect(Collectors.joining(", ")),
            consolidatedRequisitions.stream()
                .mapToInt(r -> r.getItems() != null ? r.getItems().size() : 0)
                .sum()
        );
        
        // Notify all affected users
        consolidatedRequisitions.stream()
            .map(Requisition::getUser)
            .distinct()
            .forEach(user -> {
                logger.info("CONSOLIDATION NOTIFICATION: Notifying {} about requisition consolidation", user.getUsername());
                notifySpecificUser(user, "Requisitions Consolidated", message);
            });
    }

    // Purchase Order-related notifications
    @Override
    public void notifyApproversOfNewPurchaseOrder(PurchaseOrder purchaseOrder) {
        String message = String.format(
            "New purchase order #%d requires approval. Requisition: #%d. Supplier: %s",
            purchaseOrder.getId(),
            purchaseOrder.getRequisition().getId(),
            purchaseOrder.getSupplier() != null ? purchaseOrder.getSupplier().getName() : "Not assigned"
        );
        
        logger.info("APPROVER NOTIFICATION: New purchase order {} requires approval", purchaseOrder.getId());
        notifyUsersByRole(User.Role.APPROVER, "New Purchase Order for Approval", message);
        
        auditLogService.logAction("PURCHASE_ORDER_CREATED", null,
            "Purchase Order #" + purchaseOrder.getId() + " created and sent for approval");
    }

    @Override
    public void notifyBuyerOfPurchaseOrderApproval(PurchaseOrder purchaseOrder) {
        String message = String.format(
            "Purchase order #%d has been approved and is ready for processing. Requisition: #%d. Approver: %s",
            purchaseOrder.getId(),
            purchaseOrder.getRequisition().getId(),
            purchaseOrder.getApprover() != null ? purchaseOrder.getApprover().getUsername() : "System"
        );
        
        logger.info("BUYER NOTIFICATION: Purchase order {} approved and ready for processing", purchaseOrder.getId());
        notifyUsersByRole(User.Role.BUYER, "Purchase Order Approved", message);
    }

    @Override
    public void notifyRequesterOfPurchaseOrderRejection(PurchaseOrder purchaseOrder) {
        String message = String.format(
            "Purchase order #%d for your requisition #%d has been rejected. Reason: %s",
            purchaseOrder.getId(),
            purchaseOrder.getRequisition().getId(),
            purchaseOrder.getReasonForRejection() != null ? purchaseOrder.getReasonForRejection() : "No reason provided"
        );
        
        User requester = purchaseOrder.getRequisition().getUser();
        logger.warn("REQUESTER NOTIFICATION: Notifying {} that purchase order {} has been rejected",
            requester.getUsername(), purchaseOrder.getId());
        
        notifySpecificUser(requester, "Purchase Order Rejected", message);
    }

    @Override
    public void notifyHodOfPurchaseOrderRejection(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getRequisition().getDepartment() != null && 
            purchaseOrder.getRequisition().getDepartment().getHod() != null) {
            
            String message = String.format(
                "Purchase order #%d from your department has been rejected. Requisition: #%d. Reason: %s",
                purchaseOrder.getId(),
                purchaseOrder.getRequisition().getId(),
                purchaseOrder.getReasonForRejection() != null ? purchaseOrder.getReasonForRejection() : "No reason provided"
            );
            
            User hod = purchaseOrder.getRequisition().getDepartment().getHod();
            logger.warn("HOD REJECTION NOTIFICATION: Notifying HOD {} that purchase order {} from their department has been rejected",
                hod.getUsername(), purchaseOrder.getId());
            
            notifySpecificUser(hod, "Department Purchase Order Rejected", message);
            
            auditLogService.logAction("PURCHASE_ORDER_REJECTED", hod,
                "Purchase Order #" + purchaseOrder.getId() + " rejected: " + purchaseOrder.getReasonForRejection());
        }
    }

    @Override
    public void notifySupplierOfPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getSupplier() != null) {
            String message = String.format(
                "New purchase order #%d has been sent to you. Please review and confirm availability. Contact: %s",
                purchaseOrder.getId(),
                purchaseOrder.getSupplier().getContactInfo() != null ? purchaseOrder.getSupplier().getContactInfo() : "No contact info"
            );
            
            logger.info("SUPPLIER NOTIFICATION: Purchase order {} sent to supplier {}",
                purchaseOrder.getId(), purchaseOrder.getSupplier().getName());
            
            // In a real implementation, this would send an email to the supplier
            sendEmailNotification(purchaseOrder.getSupplier().getContactInfo(), "New Purchase Order", message);
        }
    }

    // User-specific notifications
    @Override
    public void notifyUserOfRoleChange(User user, User.Role oldRole, User.Role newRole) {
        String message = String.format(
            "Your role has been changed from %s to %s. Please log out and log back in to see the changes.",
            oldRole, newRole
        );
        
        logger.info("ROLE CHANGE NOTIFICATION: User {} role changed from {} to {}", 
            user.getUsername(), oldRole, newRole);
        
        notifySpecificUser(user, "Role Updated", message);
        
        auditLogService.logAction("USER_ROLE_CHANGED", user,
            "Role changed from " + oldRole + " to " + newRole);
    }

    @Override
    public void notifyAdminOfSystemEvent(String eventType, String details) {
        String message = String.format("System Event: %s - %s", eventType, details);
        
        logger.info("SYSTEM EVENT NOTIFICATION: {} - {}", eventType, details);
        notifyUsersByRole(User.Role.ADMIN, "System Event: " + eventType, message);
        
        auditLogService.logAction("SYSTEM_EVENT", null, eventType + ": " + details);
    }

    // Email notifications (placeholder implementation)
    @Override
    public void sendEmailNotification(String to, String subject, String body) {
        // TODO: Implement actual email sending using JavaMailSender
        logger.info("EMAIL NOTIFICATION: TO={}, SUBJECT={}, BODY={}", to, subject, body);
        
        // For now, just log the email that would be sent
        System.out.println("=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("========================");
    }

    @Override
    public void sendBulkEmailNotification(List<String> recipients, String subject, String body) {
        if (recipients != null && !recipients.isEmpty()) {
            logger.info("BULK EMAIL NOTIFICATION: Recipients={}, Subject={}", recipients.size(), subject);
            recipients.forEach(recipient -> sendEmailNotification(recipient, subject, body));
        }
    }

    // Helper methods
    private void notifySpecificUser(User user, String subject, String message) {
        logger.info("NOTIFICATION: User={}, Subject={}, Message={}", user.getUsername(), subject, message);
        // TODO: Implement in-app notification storage
        // For now, we'll just log and potentially send email if user has email
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            sendEmailNotification(user.getEmail(), subject, message);
        }
    }

    private void notifyUsersByRole(User.Role role, String subject, String message) {
        List<User> users = userRepository.findAll().stream()
            .filter(user -> user.getRole() == role)
            .collect(Collectors.toList());
        
        logger.info("ROLE NOTIFICATION: Notifying {} users with role {} about: {}", users.size(), role, subject);
        
        users.forEach(user -> notifySpecificUser(user, subject, message));
    }

    private String getItemsSummary(Requisition requisition) {
        if (requisition.getItems() == null || requisition.getItems().isEmpty()) {
            return "No items specified";
        }
        
        return requisition.getItems().stream()
            .map(item -> String.format("%s (qty: %d)", item.getName(), item.getQuantity()))
            .collect(Collectors.joining(", "));
    }

    @Override
    public void notifyHodOfNewRequest(cicosy.templete.domain.RequisitionRequest savedRequest) {
        logger.info("Notifying HOD of new request: {}", savedRequest.getId());
    }

    @Override
    public void notifyUserOfRequestApproval(cicosy.templete.domain.RequisitionRequest savedRequest) {
        logger.info("Notifying user of request approval: {}", savedRequest.getId());
    }

    @Override
    public void notifyUserOfRequestRejection(cicosy.templete.domain.RequisitionRequest savedRequest) {
        logger.info("Notifying user of request rejection: {}", savedRequest.getId());
    }

    @Override
    public void notifyApproverOfNewRequisition(Requisition savedRequisition) {
        logger.info("Notifying approver of new requisition: {}", savedRequisition.getId());
    }
}