package cicosy.templete.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requisitions")
public class Requisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "requisition", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<RequisitionItem> items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String reasonForRejection;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    private boolean walkIn = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Status { PENDING_ADMIN_APPROVAL, APPROVED_BY_ADMIN, REJECTED_BY_ADMIN, CONSOLIDATED, PENDING_PO_APPROVAL, PO_APPROVED, PO_REJECTED }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public java.util.List<RequisitionItem> getItems() { return items; }
    public void setItems(java.util.List<RequisitionItem> items) { this.items = items; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getReasonForRejection() { return reasonForRejection; }
    public void setReasonForRejection(String reasonForRejection) { this.reasonForRejection = reasonForRejection; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public boolean isWalkIn() { return walkIn; }
    public void setWalkIn(boolean walkIn) { this.walkIn = walkIn; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
