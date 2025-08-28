package cicosy.templete.service.impl;

import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;
import cicosy.templete.repository.RequisitionRepository;
import cicosy.templete.service.NotificationService;
import cicosy.templete.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequisitionServiceImpl implements RequisitionService {

    @Autowired
    private RequisitionRepository requisitionRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Requisition createRequisition(Requisition requisition) {
        Requisition savedRequisition = requisitionRepository.save(requisition);
        notificationService.notifyAdminOfNewRequisition(savedRequisition);
        return savedRequisition;
    }

    @Override
    public Requisition approveRequisition(Long requisitionId) {
        Requisition requisition = requisitionRepository.findById(requisitionId).orElseThrow(() -> new RuntimeException("Requisition not found"));
        requisition.setStatus(Requisition.Status.APPROVED_BY_ADMIN);
        Requisition savedRequisition = requisitionRepository.save(requisition);
        notificationService.notifyUserOfApproval(savedRequisition);
        return savedRequisition;
    }

    @Override
    public Requisition rejectRequisition(Long requisitionId, String reason) {
        Requisition requisition = requisitionRepository.findById(requisitionId).orElseThrow(() -> new RuntimeException("Requisition not found"));
        requisition.setStatus(Requisition.Status.REJECTED_BY_ADMIN);
        requisition.setReasonForRejection(reason);
        Requisition savedRequisition = requisitionRepository.save(requisition);
        notificationService.notifyUserOfRejection(savedRequisition);
        return savedRequisition;
    }

    @Override
    public void consolidateRequisitions() {
        List<Requisition> pendingRequisitions = requisitionRepository.findByStatus(Requisition.Status.PENDING_ADMIN_APPROVAL);
        Map<String, List<Requisition>> requisitionsByItem = pendingRequisitions.stream()
                .collect(Collectors.groupingBy(Requisition::getItem));

        for (Map.Entry<String, List<Requisition>> entry : requisitionsByItem.entrySet()) {
            if (entry.getValue().size() > 2) {
                List<Requisition> toConsolidate = entry.getValue();
                int totalQuantity = toConsolidate.stream().mapToInt(Requisition::getQuantity).sum();

                Requisition consolidated = new Requisition();
                consolidated.setItem(entry.getKey());
                consolidated.setQuantity(totalQuantity);
                consolidated.setPriority(Requisition.Priority.MEDIUM); // Or some other logic for priority
                consolidated.setStatus(Requisition.Status.PENDING_PO_APPROVAL);
                consolidated.setDepartment(toConsolidate.get(0).getDepartment()); // Assuming same department
                consolidated.setUser(toConsolidate.get(0).getUser()); // Assuming same user for now

                requisitionRepository.save(consolidated);

                for (Requisition req : toConsolidate) {
                    req.setStatus(Requisition.Status.CONSOLIDATED);
                    requisitionRepository.save(req);
                }
            }
        }
    }

    @Override
    public List<Requisition> findRequisitionsForUser(User user) {
        return requisitionRepository.findByUser(user);
    }

    @Override
    public List<Requisition> findRequisitionsForHod(User hod) {
        return requisitionRepository.findByDepartment(hod.getDepartment());
    }

    @Override
    public List<Requisition> findAll() {
        return requisitionRepository.findAll();
    }

    @Override
    public Optional<Requisition> findById(Long id) {
        return requisitionRepository.findById(id);
    }
}
