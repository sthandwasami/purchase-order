package cicosy.templete.service;

import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;

import java.util.List;
import java.util.Optional;

public interface RequisitionService {
    Requisition createRequisition(Requisition requisition);
    Requisition approveRequisition(Long requisitionId);
    Requisition rejectRequisition(Long requisitionId, String reason);
    void consolidateRequisitions();
    List<Requisition> findRequisitionsForUser(User user);
    List<Requisition> findRequisitionsForHod(User hod);
    List<Requisition> findAll();
    Optional<Requisition> findById(Long id);
}
