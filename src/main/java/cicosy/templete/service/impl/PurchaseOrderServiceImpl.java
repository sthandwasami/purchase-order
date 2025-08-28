package cicosy.templete.service.impl;

import cicosy.templete.domain.PurchaseOrder;
import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;
import cicosy.templete.repository.PurchaseOrderRepository;
import cicosy.templete.repository.RequisitionRepository;
import cicosy.templete.service.NotificationService;
import cicosy.templete.service.PurchaseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderServiceImpl.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private RequisitionRepository requisitionRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        logger.info("Creating new purchase order for requisition: {}", 
            purchaseOrder.getRequisition().getId());
        
        // Validate that the requisition is in the correct state
        Requisition requisition = purchaseOrder.getRequisition();
        if (requisition.getStatus() != Requisition.Status.APPROVED_BY_ADMIN && 
            requisition.getStatus() != Requisition.Status.CONSOLIDATED) {
            throw new RuntimeException("Cannot create purchase order for requisition in status: " + requisition.getStatus());
        }
        
        // Set initial status and timestamp
        purchaseOrder.setStatus(PurchaseOrder.Status.PENDING_APPROVAL);
        purchaseOrder.setCreatedAt(LocalDateTime.now());
        
        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        
        // Update requisition status
        requisition.setStatus(Requisition.Status.PENDING_PO_APPROVAL);
        requisitionRepository.save(requisition);
        
        // Send notifications
        notificationService.notifyApproversOfNewPurchaseOrder(savedPurchaseOrder);
        
        logger.info("Purchase order {} created successfully", savedPurchaseOrder.getId());
        return savedPurchaseOrder;
    }

    @Override
    public PurchaseOrder approvePurchaseOrder(Long purchaseOrderId, User approver) {
        logger.info("Approving purchase order: {} by user: {}", purchaseOrderId, approver.getUsername());
        
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + purchaseOrderId));
        
        if (purchaseOrder.getStatus() != PurchaseOrder.Status.PENDING_APPROVAL) {
            throw new RuntimeException("Purchase order is not in a state that can be approved. Current status: " + purchaseOrder.getStatus());
        }
        
        // Validate approver role
        if (approver.getRole() != User.Role.APPROVER && approver.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("User does not have permission to approve purchase orders");
        }
        
        purchaseOrder.setStatus(PurchaseOrder.Status.APPROVED);
        purchaseOrder.setApprover(approver);
        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        
        // Update related requisition status
        Requisition requisition = savedPurchaseOrder.getRequisition();
        requisition.setStatus(Requisition.Status.PO_APPROVED);
        requisitionRepository.save(requisition);
        
        // Send notifications
        notificationService.notifyBuyerOfPurchaseOrderApproval(savedPurchaseOrder);
        
        logger.info("Purchase order {} approved successfully by {}", savedPurchaseOrder.getId(), approver.getUsername());
        return savedPurchaseOrder;
    }

    @Override
    public PurchaseOrder rejectPurchaseOrder(Long purchaseOrderId, String reason, User approver) {
        logger.info("Rejecting purchase order: {} by user: {} with reason: {}", 
            purchaseOrderId, approver.getUsername(), reason);
        
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + purchaseOrderId));
        
        if (purchaseOrder.getStatus() != PurchaseOrder.Status.PENDING_APPROVAL) {
            throw new RuntimeException("Purchase order is not in a state that can be rejected. Current status: " + purchaseOrder.getStatus());
        }
        
        // Validate approver role
        if (approver.getRole() != User.Role.APPROVER && approver.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("User does not have permission to reject purchase orders");
        }
        
        purchaseOrder.setStatus(PurchaseOrder.Status.REJECTED);
        purchaseOrder.setReasonForRejection(reason);
        purchaseOrder.setApprover(approver);
        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        
        // Update related requisition status
        Requisition requisition = savedPurchaseOrder.getRequisition();
        requisition.setStatus(Requisition.Status.PO_REJECTED);
        requisition.setReasonForRejection(reason);
        requisitionRepository.save(requisition);
        
        // Send notifications
        notificationService.notifyRequesterOfPurchaseOrderRejection(savedPurchaseOrder);
        notificationService.notifyHodOfPurchaseOrderRejection(savedPurchaseOrder);
        
        logger.info("Purchase order {} rejected successfully by {}", savedPurchaseOrder.getId(), approver.getUsername());
        return savedPurchaseOrder;
    }

    @Override
    public PurchaseOrder sendToSupplier(Long purchaseOrderId) {
        logger.info("Sending purchase order {} to supplier", purchaseOrderId);
        
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + purchaseOrderId));
        
        if (purchaseOrder.getStatus() != PurchaseOrder.Status.APPROVED) {
            throw new RuntimeException("Purchase order must be approved before sending to supplier. Current status: " + purchaseOrder.getStatus());
        }
        
        if (purchaseOrder.getSupplier() == null) {
            throw new RuntimeException("Purchase order must have a supplier assigned before sending");
        }
        
        purchaseOrder.setStatus(PurchaseOrder.Status.SENT_TO_SUPPLIER);
        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        
        // Send notification to supplier
        notificationService.notifySupplierOfPurchaseOrder(savedPurchaseOrder);
        
        logger.info("Purchase order {} sent to supplier {} successfully", 
            savedPurchaseOrder.getId(), savedPurchaseOrder.getSupplier().getName());
        return savedPurchaseOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseOrder> findById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> findByStatus(PurchaseOrder.Status status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> findPendingApproval() {
        return findByStatus(PurchaseOrder.Status.PENDING_APPROVAL);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> findApprovedPurchaseOrders() {
        return findByStatus(PurchaseOrder.Status.APPROVED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> findByRequisition(Requisition requisition) {
        return purchaseOrderRepository.findByRequisition(requisition);
    }

    @Override
    public PurchaseOrder updatePurchaseOrder(PurchaseOrder purchaseOrder) {
        logger.info("Updating purchase order: {}", purchaseOrder.getId());
        
        if (purchaseOrder.getId() == null) {
            throw new RuntimeException("Cannot update purchase order without ID");
        }
        
        PurchaseOrder existingPO = purchaseOrderRepository.findById(purchaseOrder.getId())
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + purchaseOrder.getId()));
        
        // Only allow certain fields to be updated based on status
        switch (existingPO.getStatus()) {
            case PENDING_APPROVAL:
                // Allow supplier and other details to be updated
                existingPO.setSupplier(purchaseOrder.getSupplier());
                break;
            case APPROVED:
                // Limited updates allowed
                existingPO.setSupplier(purchaseOrder.getSupplier());
                break;
            default:
                throw new RuntimeException("Cannot update purchase order in status: " + existingPO.getStatus());
        }
        
        return purchaseOrderRepository.save(existingPO);
    }

    @Override
    public void deletePurchaseOrder(Long purchaseOrderId) {
        logger.info("Deleting purchase order: {}", purchaseOrderId);
        
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + purchaseOrderId));
        
        // Only allow deletion if in certain statuses
        if (purchaseOrder.getStatus() == PurchaseOrder.Status.SENT_TO_SUPPLIER) {
            throw new RuntimeException("Cannot delete purchase order that has been sent to supplier");
        }
        
        // Update related requisition status if needed
        if (purchaseOrder.getStatus() == PurchaseOrder.Status.PENDING_APPROVAL) {
            Requisition requisition = purchaseOrder.getRequisition();
            requisition.setStatus(Requisition.Status.APPROVED_BY_ADMIN);
            requisitionRepository.save(requisition);
        }
        
        purchaseOrderRepository.delete(purchaseOrder);
        logger.info("Purchase order {} deleted successfully", purchaseOrderId);
    }
}