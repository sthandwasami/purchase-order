package cicosy.templete.service.impl;

import cicosy.templete.domain.PurchaseOrder;
import cicosy.templete.domain.Requisition;
import cicosy.templete.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void notifyUserOfApproval(Requisition requisition) {
        logger.info("Notifying user {} that requisition {} has been approved.",
                requisition.getUser().getUsername(),
                requisition.getId());
    }

    @Override
    public void notifyUserOfRejection(Requisition requisition) {
        logger.info("Notifying user {} that requisition {} has been rejected. Reason: {}",
                requisition.getUser().getUsername(),
                requisition.getId(),
                requisition.getReasonForRejection());
    }

    @Override
    public void notifyHodOfRejection(PurchaseOrder purchaseOrder) {
        logger.info("Notifying HOD {} that purchase order {} has been rejected. Reason: {}",
                purchaseOrder.getRequisition().getDepartment().getHod().getUsername(),
                purchaseOrder.getId(),
                purchaseOrder.getReasonForRejection());
    }

    @Override
    public void notifyAdminOfNewRequisition(Requisition requisition) {
        logger.info("Notifying admin that a new requisition {} has been created by user {}.",
                requisition.getId(),
                requisition.getUser().getUsername());
    }
}
