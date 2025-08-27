package cicosy.templete.dto;

import cicosy.templete.domain.PurchaseOrder;

import java.time.LocalDateTime;

public class PurchaseOrderDto {

    private Long id;
    private Long requisitionId;
    private PurchaseOrder.Status status;
    private String reasonForRejection;
    private String supplierName;
    private String approverName;
    private LocalDateTime createdAt;

    public PurchaseOrderDto(PurchaseOrder purchaseOrder) {
        this.id = purchaseOrder.getId();
        this.requisitionId = purchaseOrder.getRequisition().getId();
        this.status = purchaseOrder.getStatus();
        this.reasonForRejection = purchaseOrder.getReasonForRejection();
        if (purchaseOrder.getSupplier() != null) {
            this.supplierName = purchaseOrder.getSupplier().getName();
        }
        if (purchaseOrder.getApprover() != null) {
            this.approverName = purchaseOrder.getApprover().getUsername();
        }
        this.createdAt = purchaseOrder.getCreatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRequisitionId() { return requisitionId; }
    public void setRequisitionId(Long requisitionId) { this.requisitionId = requisitionId; }
    public PurchaseOrder.Status getStatus() { return status; }
    public void setStatus(PurchaseOrder.Status status) { this.status = status; }
    public String getReasonForRejection() { return reasonForRejection; }
    public void setReasonForRejection(String reasonForRejection) { this.reasonForRejection = reasonForRejection; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
