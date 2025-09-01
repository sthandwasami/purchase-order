package cicosy.templete.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;
    
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    
    // Reference to related entities
    private Long relatedRequestId;
    private Long relatedRequisitionId;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime readAt;
    
    public enum NotificationType {
        REQUEST_SUBMITTED,
        REQUEST_APPROVED,
        REQUEST_REJECTED,
        REQUISITION_APPROVED,
        REQUISITION_REJECTED,
        BUDGET_FEEDBACK,
        SYSTEM_UPDATE
    }
    
    public enum NotificationStatus {
        UNREAD,
        READ,
        ARCHIVED
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    public Long getRelatedRequestId() { return relatedRequestId; }
    public void setRelatedRequestId(Long relatedRequestId) { this.relatedRequestId = relatedRequestId; }
    public Long getRelatedRequisitionId() { return relatedRequisitionId; }
    public void setRelatedRequisitionId(Long relatedRequisitionId) { this.relatedRequisitionId = relatedRequisitionId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}