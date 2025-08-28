package cicosy.templete.service;

import cicosy.templete.domain.PurchaseOrder;
import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderService {
    // Core CRUD operations
    PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder);
    PurchaseOrder updatePurchaseOrder(PurchaseOrder purchaseOrder);
    void deletePurchaseOrder(Long purchaseOrderId);
    
    // Approval workflow operations
    PurchaseOrder approvePurchaseOrder(Long purchaseOrderId, User approver);
    PurchaseOrder rejectPurchaseOrder(Long purchaseOrderId, String reason, User approver);
    PurchaseOrder sendToSupplier(Long purchaseOrderId);
    
    // Query operations
    List<PurchaseOrder> findAll();
    Optional<PurchaseOrder> findById(Long id);
    List<PurchaseOrder> findByStatus(PurchaseOrder.Status status);
    List<PurchaseOrder> findPendingApproval();
    List<PurchaseOrder> findApprovedPurchaseOrders();
    List<PurchaseOrder> findByRequisition(Requisition requisition);
}