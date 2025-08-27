package cicosy.templete.service.impl;

import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.User;
import cicosy.templete.repository.RequisitionRepository;
import cicosy.templete.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequisitionServiceImpl implements RequisitionService {

    @Autowired
    private RequisitionRepository requisitionRepository;

    @Override
    public Requisition createRequisition(Requisition requisition) {
        // Logic to be implemented
        return requisitionRepository.save(requisition);
    }

    @Override
    public Requisition approveRequisition(Long requisitionId) {
        // Logic to be implemented
        return null;
    }

    @Override
    public Requisition rejectRequisition(Long requisitionId, String reason) {
        // Logic to be implemented
        return null;
    }

    @Override
    public void consolidateRequisitions() {
        // Logic to be implemented
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
