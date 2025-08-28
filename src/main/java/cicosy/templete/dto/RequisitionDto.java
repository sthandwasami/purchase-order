package cicosy.templete.dto;

import cicosy.templete.domain.Requisition;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

public class RequisitionDto {

    private Long id;
    private List<String> items;
    private int totalQuantity;
    private Requisition.Priority priority;
    private Requisition.Status status;
    private String reasonForRejection;
    private String username;
    private String departmentName;
    private LocalDateTime createdAt;

    public RequisitionDto(Requisition requisition) {
        this.id = requisition.getId();
        this.items = requisition.getItems().stream().map(item -> item.getName() + " (x" + item.getQuantity() + ")").collect(Collectors.toList());
        this.totalQuantity = requisition.getItems().stream().mapToInt(item -> item.getQuantity()).sum();
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
    public List<String> getItems() { return items; }
    public void setItems(List<String> items) { this.items = items; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
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
