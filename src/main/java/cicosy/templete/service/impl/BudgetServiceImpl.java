package cicosy.templete.service.impl;

import cicosy.templete.domain.BudgetTracking;
import cicosy.templete.domain.Department;
import cicosy.templete.repository.BudgetTrackingRepository;
import cicosy.templete.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetTrackingRepository budgetTrackingRepository;

    @Override
    @Transactional(readOnly = true)
    public BudgetTracking getCurrentBudgetStatus(Department department) {
        LocalDate now = LocalDate.now();
        return budgetTrackingRepository.findByDepartmentAndBudgetYearAndBudgetMonth(
                department, now.getYear(), now.getMonthValue())
                .orElseGet(() -> createDefaultBudgetTracking(department, now.getYear(), now.getMonthValue()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetTracking> getAllCurrentBudgetStatus() {
        LocalDate now = LocalDate.now();
        return budgetTrackingRepository.findCurrentBudgetStatus(now.getYear(), now.getMonthValue());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAfford(Department department, BigDecimal amount) {
        BudgetTracking budget = getCurrentBudgetStatus(department);
        return budget.getAvailableBudget().compareTo(amount) >= 0;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAvailableBudget(Department department) {
        return getCurrentBudgetStatus(department).getAvailableBudget();
    }

    @Override
    public void allocateBudgetForRequisition(Department department, BigDecimal amount) {
        BudgetTracking budget = getCurrentBudgetStatus(department);
        
        if (!canAfford(department, amount)) {
            throw new RuntimeException("Insufficient budget available. Required: " + amount + 
                    ", Available: " + budget.getAvailableBudget());
        }
        
        budget.setPendingBudget(budget.getPendingBudget().add(amount));
        budgetTrackingRepository.save(budget);
    }

    @Override
    public void releaseBudgetAllocation(Department department, BigDecimal amount) {
        BudgetTracking budget = getCurrentBudgetStatus(department);
        budget.setPendingBudget(budget.getPendingBudget().subtract(amount));
        
        // Ensure pending budget doesn't go negative
        if (budget.getPendingBudget().compareTo(BigDecimal.ZERO) < 0) {
            budget.setPendingBudget(BigDecimal.ZERO);
        }
        
        budgetTrackingRepository.save(budget);
    }

    @Override
    public void commitBudgetUsage(Department department, BigDecimal amount) {
        BudgetTracking budget = getCurrentBudgetStatus(department);
        
        // Move from pending to used
        budget.setPendingBudget(budget.getPendingBudget().subtract(amount));
        budget.setUsedBudget(budget.getUsedBudget().add(amount));
        
        // Ensure pending budget doesn't go negative
        if (budget.getPendingBudget().compareTo(BigDecimal.ZERO) < 0) {
            budget.setPendingBudget(BigDecimal.ZERO);
        }
        
        budgetTrackingRepository.save(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBudgetUtilizationPercentage(Department department) {
        return getCurrentBudgetStatus(department).getUtilizationPercentage();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetTracking> getDepartmentalBudgetSummary() {
        return getAllCurrentBudgetStatus();
    }

    @Override
    public BudgetTracking createOrUpdateBudget(Department department, BigDecimal totalBudget, int year, int month) {
        Optional<BudgetTracking> existingBudget = budgetTrackingRepository
                .findByDepartmentAndBudgetYearAndBudgetMonth(department, year, month);
        
        BudgetTracking budget;
        if (existingBudget.isPresent()) {
            budget = existingBudget.get();
            budget.setTotalBudget(totalBudget);
        } else {
            budget = new BudgetTracking();
            budget.setDepartment(department);
            budget.setTotalBudget(totalBudget);
            budget.setBudgetYear(year);
            budget.setBudgetMonth(month);
        }
        
        return budgetTrackingRepository.save(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BudgetTracking> findById(Long id) {
        return budgetTrackingRepository.findById(id);
    }

    private BudgetTracking createDefaultBudgetTracking(Department department, int year, int month) {
        BudgetTracking budget = new BudgetTracking();
        budget.setDepartment(department);
        budget.setTotalBudget(department.getBudgetLimit() != null ? 
                department.getBudgetLimit() : BigDecimal.ZERO);
        budget.setBudgetYear(year);
        budget.setBudgetMonth(month);
        return budgetTrackingRepository.save(budget);
    }
}