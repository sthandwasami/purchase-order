package cicosy.templete.service;

import cicosy.templete.domain.PurchaseOrder;
import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.RequisitionRequest;
import cicosy.templete.domain.User;

public interface NotificationService {
    // Requisition-related notifications
    void notifyAdminOfNewRequisition(Requisition requisition);
    void notifyUserOfRequisitionApproval(Requisition requisition);
    void notifyUserOfRequisitionRejection(Requisition requisition);
    void notifyHodOfDepartmentRequisition(Requisition requisition);
    void notifyUsersOfRequisitionConsolidation(java.util.List<Requisition> consolidatedRequisitions);
    
    // Purchase Order-related notifications
    void notifyApproversOfNewPurchaseOrder(PurchaseOrder purchaseOrder);
    void notifyBuyerOfPurchaseOrderApproval(PurchaseOrder purchaseOrder);
    void notifyRequesterOfPurchaseOrderRejection(PurchaseOrder purchaseOrder);
    void notifySupplierOfPurchaseOrder(PurchaseOrder purchaseOrder);
    void notifyHodOfPurchaseOrderRejection(PurchaseOrder purchaseOrder);
    
    // User-specific notifications
    void notifyUserOfRoleChange(User user, User.Role oldRole, User.Role newRole);
    void notifyAdminOfSystemEvent(String eventType, String details);
    
    // Email notifications (for future implementation)
    void sendEmailNotification(String to, String subject, String body);
    void sendBulkEmailNotification(java.util.List<String> recipients, String subject, String body);

    void notifyHodOfNewRequest(RequisitionRequest savedRequest);

    void notifyUserOfRequestApproval(RequisitionRequest savedRequest);

    void notifyUserOfRequestRejection(RequisitionRequest savedRequest);

    void notifyApproverOfNewRequisition(Requisition savedRequisition);
}