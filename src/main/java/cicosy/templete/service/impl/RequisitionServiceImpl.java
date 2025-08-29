package cicosy.templete.service.impl;

import cicosy.templete.domain.Department;
import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.RequisitionItem;
import cicosy.templete.domain.User;
import cicosy.templete.repository.RequisitionRepository;
import cicosy.templete.service.NotificationService;
import cicosy.templete.service.RequisitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequisitionServiceImpl implements RequisitionService {

    private static final Logger logger = LoggerFactory.getLogger(RequisitionServiceImpl.class);

    @Autowired
    private RequisitionRepository requisitionRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Requisition createRequisition(Requisition requisition) {
        logger.info("Creating new requisition for user: {}", requisition.getUser().getUsername());
        requisition.setCreatedAt(LocalDateTime.now());
        if (requisition.getItems() != null) {
            for (RequisitionItem item : requisition.getItems()) {
                item.setRequisition(requisition);
            }
        }
        Requisition savedRequisition = requisitionRepository.save(requisition);
        notificationService.notifyAdminOfNewRequisition(savedRequisition);
        if (savedRequisition.getDepartment() != null && savedRequisition.getDepartment().getHod() != null) {
            notificationService.notifyHodOfDepartmentRequisition(savedRequisition);
        }
        logger.info("Requisition {} created successfully", savedRequisition.getId());
        return savedRequisition;
    }

    @Override
    public List<Requisition> findRequisitionsForUser(User user) {
        return requisitionRepository.findByUser(user);
    }

    @Override
    public List<Requisition> findRequisitionsForDepartment(Department department) {
        return requisitionRepository.findByDepartment(department);
    }

    @Override
    public List<Requisition> findByStatus(Requisition.Status status) {
        return requisitionRepository.findByStatus(status);
    }

    @Override
    public List<Requisition> findRejectedRequisitions() {
        return requisitionRepository.findByStatus(Requisition.Status.REJECTED_BY_ADMIN);
    }

    @Override
    public Requisition sendToProcurement(Long requisitionId) {
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Requisition not found with id: " + requisitionId));
        requisition.setStatus(Requisition.Status.PENDING_PO_APPROVAL);
        return requisitionRepository.save(requisition);
    }

    @Override
    public Requisition markAsCompleted(Long requisitionId, BigDecimal actualCost) {
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Requisition not found with id: " + requisitionId));
        requisition.setStatus(Requisition.Status.PO_APPROVED);
        return requisitionRepository.save(requisition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Requisition> findRequisitionsForHod(User hod) {
        if (hod.getDepartment() == null) {
            logger.warn("HOD {} does not have a department assigned", hod.getUsername());
            return List.of();
        }
        return requisitionRepository.findByDepartment(hod.getDepartment());
    }

    @Override
    public Requisition approveRequisition(Long id) {
        Requisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found with id: " + id));
        requisition.setStatus(Requisition.Status.APPROVED_BY_ADMIN);
        notificationService.notifyUserOfRequisitionApproval(requisition);
        return requisitionRepository.save(requisition);
    }

    @Override
    public Requisition rejectRequisition(Long id, String reason) {
        Requisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found with id: " + id));
        requisition.setStatus(Requisition.Status.REJECTED_BY_ADMIN);
        requisition.setReasonForRejection(reason);
        notificationService.notifyUserOfRequisitionRejection(requisition);
        return requisitionRepository.save(requisition);
    }

    @Override
    public void consolidateRequisitions() {
        logger.info("Starting requisition consolidation process");
        
        List<Requisition> pendingRequisitions = requisitionRepository.findByStatus(Requisition.Status.APPROVED_BY_ADMIN);
        
        if (pendingRequisitions.isEmpty()) {
            logger.info("No approved requisitions found for consolidation");
            return;
        }
        
        // Group requisitions by similar items (by name and category)
        Map<String, List<Requisition>> consolidationGroups = groupRequisitionsForConsolidation(pendingRequisitions);
        
        for (Map.Entry<String, List<Requisition>> group : consolidationGroups.entrySet()) {
            List<Requisition> requisitionsToConsolidate = group.getValue();
            
            // Only consolidate if we have multiple requisitions with similar items
            if (requisitionsToConsolidate.size() > 1) {
                logger.info("Consolidating {} requisitions for item group: {}", 
                    requisitionsToConsolidate.size(), group.getKey());
                
                // Create consolidated requisition
                Requisition consolidatedRequisition = createConsolidatedRequisition(requisitionsToConsolidate);
                requisitionRepository.save(consolidatedRequisition);
                
                // Update status of original requisitions
                for (Requisition req : requisitionsToConsolidate) {
                    req.setStatus(Requisition.Status.CONSOLIDATED);
                    requisitionRepository.save(req);
                }
                
                // Notify users about consolidation
                notificationService.notifyUsersOfRequisitionConsolidation(requisitionsToConsolidate);
            }
        }
        
        logger.info("Requisition consolidation process completed");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Requisition> findAll() {
        return requisitionRepository.findAll();
    }

    @Override
    public Requisition approveRequisitionByBudget(Long requisitionId, User approver) {
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Requisition not found with id: " + requisitionId));
        requisition.setStatus(Requisition.Status.PO_APPROVED);
        return requisitionRepository.save(requisition);
    }

    @Override
    public Requisition rejectRequisitionByBudget(Long requisitionId, String reason, User approver) {
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Requisition not found with id: " + requisitionId));
        requisition.setStatus(Requisition.Status.PO_REJECTED);
        requisition.setReasonForRejection(reason);
        return requisitionRepository.save(requisition);
    }

    @Override
    public List<Requisition> findRequisitionsAwaitingBudgetApproval() {
        return requisitionRepository.findByStatus(Requisition.Status.AWAITING_PO_APPROVAL);
    }

    @Override
    public List<Requisition> findApprovedRequisitions() {
        return requisitionRepository.findByStatus(Requisition.Status.APPROVED_BY_ADMIN);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Requisition> findById(Long id) {
        return requisitionRepository.findById(id);
    }

    @Override
    public Requisition save(Requisition requisition) {
        return requisitionRepository.save(requisition);
    }
    
    @Override
    public Requisition approveBudget(Long requisitionId, User approver, String comments) {
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Requisition not found with id: " + requisitionId));
        
        if (requisition.getStatus() != Requisition.Status.AWAITING_PO_APPROVAL) {
            throw new RuntimeException("Requisition is not awaiting budget approval");
        }
        
        requisition.setStatus(Requisition.Status.PO_APPROVED);
        Requisition savedRequisition = requisitionRepository.save(requisition);
        
        // Notify HOD and user of budget approval
        notifyBudgetDecision(savedRequisition, true, null, comments);
        
        logger.info("Budget approved for requisition {} by approver {}", requisitionId, approver.getUsername());
        return savedRequisition;
    }
    
    @Override
    public Requisition rejectBudget(Long requisitionId, String reason, User approver, String comments) {
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Requisition not found with id: " + requisitionId));
        
        if (requisition.getStatus() != Requisition.Status.AWAITING_PO_APPROVAL) {
            throw new RuntimeException("Requisition is not awaiting budget approval");
        }
        
        requisition.setStatus(Requisition.Status.PO_REJECTED);
        requisition.setReasonForRejection(reason);
        Requisition savedRequisition = requisitionRepository.save(requisition);
        
        // Notify HOD and user of budget rejection
        notifyBudgetDecision(savedRequisition, false, reason, comments);
        
        logger.info("Budget rejected for requisition {} by approver {}. Reason: {}", 
            requisitionId, approver.getUsername(), reason);
        return savedRequisition;
    }
    
    private void notifyBudgetDecision(Requisition requisition, boolean approved, String reason, String comments) {
        // Notify the original requester
        String message = String.format(
            "Budget %s for your requisition #%d. %s %s",
            approved ? "approved" : "rejected",
            requisition.getId(),
            reason != null ? "Reason: " + reason : "",
            comments != null ? "Comments: " + comments : ""
        );
        
        // In a real implementation, you would send notifications here
        logger.info("Notifying user {} about budget decision for requisition {}", 
            requisition.getUser().getUsername(), requisition.getId());
        
        // Notify HOD if different from requester
        if (requisition.getDepartment() != null && 
            requisition.getDepartment().getHod() != null &&
            !requisition.getDepartment().getHod().equals(requisition.getUser())) {
            
            logger.info("Notifying HOD {} about budget decision for requisition {}", 
                requisition.getDepartment().getHod().getUsername(), requisition.getId());
        }
    }

    // Helper methods
    private Map<String, List<Requisition>> groupRequisitionsForConsolidation(List<Requisition> requisitions) {
        return requisitions.stream()
            .filter(req -> req.getItems() != null && !req.getItems().isEmpty())
            .collect(Collectors.groupingBy(this::getConsolidationKey));
    }

    private String getConsolidationKey(Requisition requisition) {
        // Create a key based on the first item's name and category
        // In a real implementation, you might want more sophisticated grouping logic
        if (requisition.getItems() != null && !requisition.getItems().isEmpty()) {
            RequisitionItem firstItem = requisition.getItems().get(0);
            return String.format("%s_%s_%s", 
                firstItem.getName().toLowerCase(),
                firstItem.getCategory() != null ? firstItem.getCategory().toLowerCase() : "general",
                requisition.getPriority().name());
        }
        return "unknown_" + requisition.getId();
    }

    private Requisition createConsolidatedRequisition(List<Requisition> requisitionsToConsolidate) {
        if (requisitionsToConsolidate.isEmpty()) {
            throw new IllegalArgumentException("Cannot create consolidated requisition from empty list");
        }

        Requisition consolidated = new Requisition();
        
        // Use the first requisition's properties as base
        Requisition baseRequisition = requisitionsToConsolidate.get(0);
        consolidated.setDepartment(baseRequisition.getDepartment());
        consolidated.setPriority(baseRequisition.getPriority());
        consolidated.setUser(baseRequisition.getUser()); // System user in a real implementation
        consolidated.setStatus(Requisition.Status.PENDING_PO_APPROVAL);
        consolidated.setCreatedAt(LocalDateTime.now());
        
        // Consolidate items
        Map<String, RequisitionItem> consolidatedItems = requisitionsToConsolidate.stream()
            .flatMap(req -> req.getItems().stream())
            .collect(Collectors.groupingBy(
                item -> item.getName() + "_" + (item.getCategory() != null ? item.getCategory() : ""),
                Collectors.reducing(null, item -> {
                    RequisitionItem consolidated_item = new RequisitionItem();
                    consolidated_item.setName(item.getName());
                    consolidated_item.setCategory(item.getCategory());
                    consolidated_item.setSpecifications(item.getSpecifications());
                    consolidated_item.setQuantity(0);
                    consolidated_item.setRequisition(consolidated);
                    return consolidated_item;
                }, (item1, item2) -> {
                    if (item1 == null) return item2;
                    if (item2 == null) return item1;
                    item1.setQuantity(item1.getQuantity() + item2.getQuantity());
                    return item1;
                })
            ));

        // Sum up quantities for same items
        requisitionsToConsolidate.stream()
            .flatMap(req -> req.getItems().stream())
            .forEach(item -> {
                String key = item.getName() + "_" + (item.getCategory() != null ? item.getCategory() : "");
                RequisitionItem consolidatedItem = consolidatedItems.get(key);
                if (consolidatedItem != null) {
                    consolidatedItem.setQuantity(consolidatedItem.getQuantity() + item.getQuantity());
                }
            });

        consolidated.setItems(consolidatedItems.values().stream().collect(Collectors.toList()));
        
        return consolidated;
    }
}