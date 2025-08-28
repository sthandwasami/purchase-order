package cicosy.templete.repository;

import cicosy.templete.domain.Department;
import cicosy.templete.domain.RequisitionRequest;
import cicosy.templete.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionRequestRepository extends JpaRepository<RequisitionRequest, Long> {
    
    List<RequisitionRequest> findByUser(User user);
    
    List<RequisitionRequest> findByDepartment(Department department);
    
    List<RequisitionRequest> findByStatus(RequisitionRequest.Status status);
    
    List<RequisitionRequest> findByDepartmentAndStatus(Department department, RequisitionRequest.Status status);
    
    // Find pending requests for HOD review by department
    @Query("SELECT r FROM RequisitionRequest r WHERE r.department = :department AND r.status = 'PENDING_HOD_REVIEW' ORDER BY r.priority DESC, r.createdAt ASC")
    List<RequisitionRequest> findPendingRequestsForHod(@Param("department") Department department);
    
    // Find requests that can be consolidated (approved by HOD, same item)
    @Query("SELECT r FROM RequisitionRequest r WHERE r.status = 'APPROVED_BY_HOD' AND r.item = :item AND r.department = :department")
    List<RequisitionRequest> findConsolidatableRequests(@Param("item") String item, @Param("department") Department department);
    
    // Count pending requests by department
    @Query("SELECT COUNT(r) FROM RequisitionRequest r WHERE r.department = :department AND r.status = 'PENDING_HOD_REVIEW'")
    long countPendingRequestsByDepartment(@Param("department") Department department);
    
    // Find recent activity for a department
    @Query("SELECT r FROM RequisitionRequest r WHERE r.department = :department ORDER BY r.updatedAt DESC")
    List<RequisitionRequest> findRecentActivityByDepartment(@Param("department") Department department);
}