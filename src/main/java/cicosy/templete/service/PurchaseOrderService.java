package cicosy.templete.service;

import cicosy.templete.domain.PurchaseOrder;
import cicosy.templete.domain.User;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderService {
    PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder);
    PurchaseOrder approvePurchaseOrder(Long purchaseOrderId, User approver);
    PurchaseOrder rejectPurchaseOrder(Long purchaseOrderId, String reason, User approver);
    PurchaseOrder sendToSupplier(Long purchaseOrderId);
    List<PurchaseOrder> findAll();
    Optional<PurchaseOrder> findById(Long id);
}
