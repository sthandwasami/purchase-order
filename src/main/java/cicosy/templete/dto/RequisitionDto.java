package cicosy.templete.dto;

import cicosy.templete.domain.Requisition;

import java.time.LocalDateTime;

public class RequisitionDto {

    private Long id;
    private String item;
    private int quantity;
    private Requisition.Priority priority;
    private Requisition.Status status;
    private String reasonForRejection;
    private String username;
    private String departmentName;
    private LocalDateTime createdAt;

    public RequisitionDto(Requisition requisition) {
        this.id = requisition.getId();
        this.item = requisition.getItem();
        this.quantity = requisition.getQuantity();
        this.priority = requisition.getPriority();
        this.status = requisition.getStatus();
        this.reasonForRejection = requisition.getReasonForRejection();
        this.username = requisition.getUser().getUsername();
        this.departmentName = requisition.getDepartment().getName();
        this.createdAt = requisition.getCreatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Requisition.Priority getPriority() { return priority; }
    public void setPriority(Requisition.Priority priority) { this.priority = priority; }
    public Requisition.Status getStatus() { return status; }
    public void setStatus(Requisition.Status status) { this.status = status; }
    public String getReasonForRejection() { return reasonForRejection; }
    public void setReasonForRejection(String reasonForRejection) { this.reasonForRejection = reasonForRejection; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
