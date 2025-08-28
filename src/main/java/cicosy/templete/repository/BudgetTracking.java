package cicosy.templete.repository;

import cicosy.templete.domain.Department;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_tracking")
public class BudgetTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal totalBudget;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal usedBudget = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal pendingBudget = BigDecimal.ZERO; // Budget allocated to pending requisitions

    @Column(nullable = false)
    private int budgetYear;

    @Column(nullable = false)
    private int budgetMonth;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public BigDecimal getAvailableBudget() {
        return totalBudget.subtract(usedBudget).subtract(pendingBudget);
    }

    public BigDecimal getUtilizationPercentage() {
        if (totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return usedBudget.add(pendingBudget)
                .divide(totalBudget, 2, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
    public BigDecimal getUsedBudget() { return usedBudget; }
    public void setUsedBudget(BigDecimal usedBudget) { this.usedBudget = usedBudget; }
    public BigDecimal getPendingBudget() { return pendingBudget; }
    public void setPendingBudget(BigDecimal pendingBudget) { this.pendingBudget = pendingBudget; }
    public int getBudgetYear() { return budgetYear; }
    public void setBudgetYear(int budgetYear) { this.budgetYear = budgetYear; }
    public int getBudgetMonth() { return budgetMonth; }
    public void setBudgetMonth(int budgetMonth) { this.budgetMonth = budgetMonth; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}