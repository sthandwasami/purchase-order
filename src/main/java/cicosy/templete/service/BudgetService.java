package cicosy.templete.service;

import cicosy.templete.domain.BudgetTracking;
import cicosy.templete.domain.Department;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BudgetService {
    
    // Budget tracking operations
    BudgetTracking getCurrentBudgetStatus(Department department);
    List<BudgetTracking> getAllCurrentBudgetStatus();
    
    // Budget validation
    boolean canAfford(Department department, BigDecimal amount);
    BigDecimal getAvailableBudget(Department department);
    
    // Budget allocation operations
    void allocateBudgetForRequisition(Department department, BigDecimal amount);
    void releaseBudgetAllocation(Department department, BigDecimal amount);
    void commitBudgetUsage(Department department, BigDecimal amount);
    
    // Budget reporting
    BigDecimal getBudgetUtilizationPercentage(Department department);
    List<BudgetTracking> getDepartmentalBudgetSummary();
    
    // Administrative operations
    BudgetTracking createOrUpdateBudget(Department department, BigDecimal totalBudget, int year, int month);
    Optional<BudgetTracking> findById(Long id);
}