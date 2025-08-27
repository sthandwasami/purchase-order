package cicosy.templete.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal budgetLimit;

    @OneToOne
    @JoinColumn(name = "hod_id")
    private User hod;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; }
    public User getHod() { return hod; }
    public void setHod(User hod) { this.hod = hod; }
}


