// NotificationDto.java
package cicosy.templete.dto;

import java.time.LocalDateTime;

public class NotificationDto {
    private Long id;
    private String subject;
    private String message;
    private String recipientUsername;
    private String recipientEmail;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public enum NotificationType {
        REQUISITION_CREATED,
        REQUISITION_APPROVED,
        REQUISITION_REJECTED,
        REQUISITION_CONSOLIDATED,
        PURCHASE_ORDER_CREATED,
        PURCHASE_ORDER_APPROVED,
        PURCHASE_ORDER_REJECTED,
        PURCHASE_ORDER_SENT_TO_SUPPLIER,
        SYSTEM_NOTIFICATION,
        USER_ROLE_CHANGED
    }

    public enum NotificationStatus {
        UNREAD,
        READ,
        ARCHIVED
    }
}