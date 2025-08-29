package cicosy.templete.service.impl;

import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.RequisitionRequest;
import cicosy.templete.domain.User;
import cicosy.templete.repository.RequisitionRepository;
import cicosy.templete.repository.RequisitionRequestRepository;
import cicosy.templete.service.NotificationService;
import cicosy.templete.service.RequisitionRequestService;
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
public class RequisitionRequestServiceImpl implements RequisitionRequestService {

    @Autowired
    private RequisitionRequestRepository requestRepository;

    @Autowired
    private RequisitionRepository requisitionRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public RequisitionRequest createRequest(RequisitionRequest request) {
        request.setStatus(RequisitionRequest.Status.PENDING_HOD_REVIEW);
        RequisitionRequest savedRequest = requestRepository.save(request);
        
        // Notify HOD of new request
        notificationService.notifyHodOfNewRequest(savedRequest);
        
        return savedRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequisitionRequest> findRequestsForUser(User user) {
        return requestRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequisitionRequest> findPendingRequestsForHod(User hod) {
        return requestRepository.findAllPendingRequestsForHod(hod.getDepartment());
    }

    @Override
    public RequisitionRequest approveRequest(Long requestId, User hod) {
        return approveRequest(requestId, hod, null);
    }
    
    @Override
    public RequisitionRequest approveRequest(Long requestId, User hod, String comments) {
        RequisitionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        if (!request.getDepartment().equals(hod.getDepartment()) && 
            !(request.getUser().getRole() == User.Role.HOD && request.getUser().getDepartment().equals(hod.getDepartment()))) {
            throw new RuntimeException("HOD can only approve requests from their department");
        }
        
        request.setStatus(RequisitionRequest.Status.APPROVED_BY_HOD);
        request.setReviewedByHod(hod);
        request.setHodReviewedAt(LocalDateTime.now());
        
        RequisitionRequest savedRequest = requestRepository.save(request);
        
        // Automatically convert approved request to requisition for approver review
        convertApprovedRequestToRequisition(savedRequest);
        
        // Notify user of approval
        notificationService.notifyUserOfRequestApproval(savedRequest, comments);
        
        return savedRequest;
    }

    @Override
    public RequisitionRequest rejectRequest(Long requestId, String reason, User hod) {
        return rejectRequest(requestId, reason, hod, null);
    }
    
    @Override
    public RequisitionRequest rejectRequest(Long requestId, String reason, User hod, String comments) {
        RequisitionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        if (!request.getDepartment().equals(hod.getDepartment())) {
            throw new RuntimeException("HOD can only reject requests from their department");
        }
        
        request.setStatus(RequisitionRequest.Status.REJECTED_BY_HOD);
        request.setReasonForRejection(reason);
        request.setReviewedByHod(hod);
        request.setHodReviewedAt(LocalDateTime.now());
        
        RequisitionRequest savedRequest = requestRepository.save(request);
        
        // Notify user of rejection
        notificationService.notifyUserOfRequestRejection(savedRequest, comments);
        
        return savedRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequisitionRequest> findDepartmentRequests(User hod) {
        return requestRepository.findByDepartment(hod.getDepartment());
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingRequestsForDepartment(User hod) {
        return requestRepository.countPendingRequestsByDepartment(hod.getDepartment());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequisitionRequest> findRecentDepartmentActivity(User hod) {
        return requestRepository.findRecentActivityByDepartment(hod.getDepartment())
                .stream()
                .limit(10) // Last 10 activities
                .collect(Collectors.toList());
    }

    @Override
    public void consolidateApprovedRequests() {
        // Find all approved requests grouped by item and department
        List<RequisitionRequest> approvedRequests = requestRepository.findByStatus(
                RequisitionRequest.Status.APPROVED_BY_HOD);
        
        Map<String, List<RequisitionRequest>> groupedRequests = approvedRequests.stream()
                .collect(Collectors.groupingBy(request -> 
                    request.getDepartment().getId() + ":" + request.getItem().toLowerCase().trim()));
        
        for (Map.Entry<String, List<RequisitionRequest>> entry : groupedRequests.entrySet()) {
            List<RequisitionRequest> requests = entry.getValue();
            
            if (requests.size() > 1) { // Only consolidate if there are multiple similar requests
                consolidateRequestsIntoRequisition(requests);
            } else {
                // Single request - convert directly to requisition
                convertSingleRequestToRequisition(requests.get(0));
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequisitionRequest> findConsolidatableRequests(String item, User hod) {
        return requestRepository.findConsolidatableRequests(item, hod.getDepartment());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequisitionRequest> findAll() {
        return requestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RequisitionRequest> findById(Long id) {
        return requestRepository.findById(id);
    }

    @Override
    public RequisitionRequest save(RequisitionRequest request) {
        return requestRepository.save(request);
    }

    private void consolidateRequestsIntoRequisition(List<RequisitionRequest> requests) {
        if (requests.isEmpty()) return;

        RequisitionRequest firstRequest = requests.get(0);

        // Create consolidated requisition
        Requisition requisition = new Requisition();
        
        List<cicosy.templete.domain.RequisitionItem> items = requests.stream().map(request -> {
            cicosy.templete.domain.RequisitionItem item = new cicosy.templete.domain.RequisitionItem();
            item.setName(request.getItem());
            item.setQuantity(request.getQuantity());
            item.setSpecifications(request.getDescription());
            item.setRequisition(requisition);
            return item;
        }).collect(Collectors.toList());
        
        requisition.setItems(items);

        // Set highest priority
        Requisition.Priority highestPriority = requests.stream()
                .map(r -> Requisition.Priority.valueOf(r.getPriority().name()))
                .min((p1, p2) -> p1.ordinal() - p2.ordinal()) // HIGH=0, MEDIUM=1, LOW=2
                .orElse(Requisition.Priority.MEDIUM);
        requisition.setPriority(highestPriority);

        requisition.setDepartment(firstRequest.getDepartment());
        requisition.setUser(firstRequest.getUser());
        requisition.setStatus(Requisition.Status.AWAITING_PO_APPROVAL);
        
        // Save requisition
        Requisition savedRequisition = requisitionRepository.save(requisition);
        
        // Update original requests status
        for (RequisitionRequest request : requests) {
            request.setStatus(RequisitionRequest.Status.CONSOLIDATED);
            requestRepository.save(request);
        }
        
        // Notify approver of new requisition
        notificationService.notifyApproverOfNewRequisition(savedRequisition);
    }

    private void convertSingleRequestToRequisition(RequisitionRequest request) {
        Requisition requisition = new Requisition();
        cicosy.templete.domain.RequisitionItem item = new cicosy.templete.domain.RequisitionItem();
        item.setName(request.getItem());
        item.setQuantity(request.getQuantity());
        item.setSpecifications(request.getDescription());
        item.setRequisition(requisition);

        requisition.setItems(java.util.Collections.singletonList(item));
        requisition.setPriority(Requisition.Priority.valueOf(request.getPriority().name()));
        requisition.setDepartment(request.getDepartment());
        requisition.setUser(request.getUser());
        requisition.setStatus(Requisition.Status.AWAITING_PO_APPROVAL);
        
        // Save requisition
        Requisition savedRequisition = requisitionRepository.save(requisition);
        
        // Update original request status
        request.setStatus(RequisitionRequest.Status.CONVERTED_TO_REQUISITION);
        requestRepository.save(request);
        
        // Notify approver of new requisition
        notificationService.notifyApproverOfNewRequisition(savedRequisition);
    }
    
    private void convertApprovedRequestToRequisition(RequisitionRequest request) {
        Requisition requisition = new Requisition();
        cicosy.templete.domain.RequisitionItem item = new cicosy.templete.domain.RequisitionItem();
        item.setName(request.getItem());
        item.setQuantity(request.getQuantity());
        item.setSpecifications(request.getDescription());
        item.setRequisition(requisition);

        requisition.setItems(java.util.Collections.singletonList(item));
        requisition.setPriority(Requisition.Priority.valueOf(request.getPriority().name()));
        requisition.setDepartment(request.getDepartment());
        requisition.setUser(request.getUser());
        requisition.setStatus(Requisition.Status.AWAITING_PO_APPROVAL);
        requisition.setWalkIn(request.isWalkIn());
        
        // Save requisition
        Requisition savedRequisition = requisitionRepository.save(requisition);
        
        // Update original request status
        request.setStatus(RequisitionRequest.Status.CONVERTED_TO_REQUISITION);
        requestRepository.save(request);
        
        // Notify approver of new requisition
        notificationService.notifyApproverOfNewRequisition(savedRequisition);
    }
}