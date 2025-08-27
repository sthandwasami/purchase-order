package cicosy.templete.service;

import cicosy.templete.domain.PurchaseOrder;
import cicosy.templete.domain.Requisition;

public interface NotificationService {
    void notifyUserOfRejection(Requisition requisition);
    void notifyHodOfRejection(PurchaseOrder purchaseOrder);
}
