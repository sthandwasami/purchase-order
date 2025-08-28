package cicosy.templete.repository;

import cicosy.templete.domain.BudgetTracking;
import cicosy.templete.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetTrackingRepository extends JpaRepository<BudgetTracking, Long> {
    
    Optional<BudgetTracking> findByDepartmentAndBudgetYearAndBudgetMonth(
            Department department, int budgetYear, int budgetMonth);
    
    List<BudgetTracking> findByBudgetYearAndBudgetMonth(int budgetYear, int budgetMonth);
    
    List<BudgetTracking> findByDepartment(Department department);
    
    @Query("SELECT bt FROM BudgetTracking bt WHERE bt.budgetYear = :year AND bt.budgetMonth = :month ORDER BY bt.department.name")
    List<BudgetTracking> findCurrentBudgetStatus(@Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(bt.totalBudget) FROM BudgetTracking bt WHERE bt.budgetYear = :year AND bt.budgetMonth = :month")
    java.math.BigDecimal getTotalOrganizationalBudget(@Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(bt.usedBudget) FROM BudgetTracking bt WHERE bt.budgetYear = :year AND bt.budgetMonth = :month")
    java.math.BigDecimal getTotalUsedBudget(@Param("year") int year, @Param("month") int month);
}