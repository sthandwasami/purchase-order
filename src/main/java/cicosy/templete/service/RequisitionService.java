package cicosy.templete.service;

import cicosy.templete.domain.Department;
import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;

import java.util.List;
import java.util.Optional;

public interface RequisitionService {
    
    // Creation and basic operations
    Requisition createRequisition(Requisition requisition);
    Optional<Requisition> findById(Long id);
    Requisition save(Requisition requisition);
    List<Requisition> findAll();
    
    // Budget approval operations
    Requisition approveRequisitionByBudget(Long requisitionId, User approver);
    Requisition rejectRequisitionByBudget(Long requisitionId, String reason, User approver);
    
    // Query operations for different roles
    List<Requisition> findRequisitionsAwaitingBudgetApproval();
    List<Requisition> findApprovedRequisitions();
    List<Requisition> findRequisitionsForUser(User user);
    List<Requisition> findRequisitionsForDepartment(Department department);
    
    // Status-based queries
    List<Requisition> findByStatus(Requisition.Status status);
    List<Requisition> findRejectedRequisitions();
    List<Requisition> findRequisitionsForHod(User user);
    Requisition approveRequisition(Long id);
    Requisition rejectRequisition(Long id, String reason);
    void consolidateRequisitions();
    
    // Budget approval operations (for APPROVER role)
    Requisition approveBudget(Long requisitionId, User approver, String comments);
    Requisition rejectBudget(Long requisitionId, String reason, User approver, String comments);
    
    // Procurement operations
    Requisition sendToProcurement(Long requisitionId);
    Requisition markAsCompleted(Long requisitionId, java.math.BigDecimal actualCost);
}