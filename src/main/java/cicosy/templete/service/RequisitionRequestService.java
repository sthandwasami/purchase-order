package cicosy.templete.service;

import cicosy.templete.domain.RequisitionRequest;
import cicosy.templete.domain.User;

import java.util.List;
import java.util.Optional;

public interface RequisitionRequestService {
    
    // User operations
    RequisitionRequest createRequest(RequisitionRequest request);
    List<RequisitionRequest> findRequestsForUser(User user);
    
    // HOD operations
    List<RequisitionRequest> findPendingRequestsForHod(User hod);
    RequisitionRequest approveRequest(Long requestId, User hod);
    RequisitionRequest approveRequest(Long requestId, User hod, String comments);
    RequisitionRequest rejectRequest(Long requestId, String reason, User hod);
    RequisitionRequest rejectRequest(Long requestId, String reason, User hod, String comments);
    List<RequisitionRequest> findDepartmentRequests(User hod);
    long countPendingRequestsForDepartment(User hod);
    List<RequisitionRequest> findRecentDepartmentActivity(User hod);
    
    // Consolidation operations
    void consolidateApprovedRequests();
    List<RequisitionRequest> findConsolidatableRequests(String item, User hod);
    
    // General operations
    List<RequisitionRequest> findAll();
    Optional<RequisitionRequest> findById(Long id);
    RequisitionRequest save(RequisitionRequest request);
}