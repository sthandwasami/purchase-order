package cicosy.templete.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType = LoginType.INDIVIDUAL;
    
    // Current session login type (not persisted)
    @Transient
    private LoginType currentSessionLoginType;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // New field to track approval limits for approvers
    @Column(precision = 15, scale = 2)
    private java.math.BigDecimal approvalLimit;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public LoginType getLoginType() { return loginType; }
    public void setLoginType(LoginType loginType) { this.loginType = loginType; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public java.math.BigDecimal getApprovalLimit() { return approvalLimit; }
    public void setApprovalLimit(java.math.BigDecimal approvalLimit) { this.approvalLimit = approvalLimit; }
    public LoginType getCurrentSessionLoginType() { return currentSessionLoginType; }
    public void setCurrentSessionLoginType(LoginType currentSessionLoginType) { this.currentSessionLoginType = currentSessionLoginType; }

    public enum Role { USER, ADMIN, HOD, BUYER, APPROVER }
    public enum LoginType { INDIVIDUAL, DEPARTMENT }
}