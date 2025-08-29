package cicosy.templete.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "requisition_requests")
public class RequisitionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String item;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String businessJustification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING_HOD_REVIEW;

    private String reasonForRejection;

    @Column(precision = 15, scale = 2)
    private BigDecimal estimatedCost;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "hod_id")
    private User reviewedByHod;

    // Link to the requisition this request was consolidated into
    @ManyToOne
    @JoinColumn(name = "consolidated_into_id")
    private Requisition consolidatedInto;

    private LocalDateTime hodReviewedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private boolean walkIn = false;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Status { 
        PENDING_HOD_REVIEW, 
        APPROVED_BY_HOD, 
        REJECTED_BY_HOD,
        CONSOLIDATED,
        CONVERTED_TO_REQUISITION
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBusinessJustification() { return businessJustification; }
    public void setBusinessJustification(String businessJustification) { this.businessJustification = businessJustification; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getReasonForRejection() { return reasonForRejection; }
    public void setReasonForRejection(String reasonForRejection) { this.reasonForRejection = reasonForRejection; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public User getReviewedByHod() { return reviewedByHod; }
    public void setReviewedByHod(User reviewedByHod) { this.reviewedByHod = reviewedByHod; }
    public Requisition getConsolidatedInto() { return consolidatedInto; }
    public void setConsolidatedInto(Requisition consolidatedInto) { this.consolidatedInto = consolidatedInto; }
    public LocalDateTime getHodReviewedAt() { return hodReviewedAt; }
    public void setHodReviewedAt(LocalDateTime hodReviewedAt) { this.hodReviewedAt = hodReviewedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isWalkIn() { return walkIn; }
    public void setWalkIn(boolean walkIn) { this.walkIn = walkIn; }
}