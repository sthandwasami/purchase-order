package cicosy.templete.repository;

import cicosy.templete.domain.PurchaseOrder;
import cicosy.templete.domain.Requisition;
import cicosy.templete.domain.Supplier;
import cicosy.templete.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    
    // Find by status
    List<PurchaseOrder> findByStatus(PurchaseOrder.Status status);
    
    // Find by requisition
    List<PurchaseOrder> findByRequisition(Requisition requisition);
    
    // Find by supplier
    List<PurchaseOrder> findBySupplier(Supplier supplier);
    
    // Find by approver
    List<PurchaseOrder> findByApprover(User approver);
    
    // Find by date range
    List<PurchaseOrder> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Find pending approval ordered by creation date
    List<PurchaseOrder> findByStatusOrderByCreatedAtAsc(PurchaseOrder.Status status);
    
    // Find recent purchase orders (last 30 days)
    @Query("SELECT po FROM PurchaseOrder po WHERE po.createdAt >= :thirtyDaysAgo ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findRecentPurchaseOrders(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
    
    // Find purchase orders by department (through requisition)
    @Query("SELECT po FROM PurchaseOrder po WHERE po.requisition.department.id = :departmentId")
    List<PurchaseOrder> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Count by status
    long countByStatus(PurchaseOrder.Status status);
    
    // Find by multiple statuses
    @Query("SELECT po FROM PurchaseOrder po WHERE po.status IN :statuses")
    List<PurchaseOrder> findByStatusIn(@Param("statuses") List<PurchaseOrder.Status> statuses);
    
    // Find overdue purchase orders (pending for more than specified days)
    @Query("SELECT po FROM PurchaseOrder po WHERE po.status = :status AND po.createdAt < :cutoffDate")
    List<PurchaseOrder> findOverduePurchaseOrders(@Param("status") PurchaseOrder.Status status, 
                                                   @Param("cutoffDate") LocalDateTime cutoffDate);
}