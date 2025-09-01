package cicosy.templete.config;

import cicosy.templete.domain.Department;
import cicosy.templete.domain.User;
import cicosy.templete.repository.DepartmentRepository;
import cicosy.templete.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Lazy
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Always ensure test users exist
        initializeTestData();
    }

    private void initializeTestData() {
        // Create departments if they don't exist
        Department itDept = departmentRepository.findByName("IT")
                .orElseGet(() -> {
                    Department dept = new Department();
                    dept.setName("IT");
                    dept.setBudgetLimit(new BigDecimal("50000"));
                    return departmentRepository.save(dept);
                });

        Department hrDept = departmentRepository.findByName("HR")
                .orElseGet(() -> {
                    Department dept = new Department();
                    dept.setName("HR");
                    dept.setBudgetLimit(new BigDecimal("30000"));
                    return departmentRepository.save(dept);
                });

        // Create users with encoded passwords if they don't exist
        String encodedPassword = passwordEncoder.encode("Password1!");

        // HOD
        if (!userRepository.findByUsername("hod1").isPresent()) {
            User hod1 = new User();
            hod1.setUsername("hod1");
            hod1.setEmail("hod1@example.com");
            hod1.setPassword(encodedPassword);
            hod1.setRole(User.Role.HOD);
            hod1.setDepartment(itDept);
            hod1.setEnabled(true);
            userRepository.save(hod1);
            
            // Set HOD for department
            itDept.setHod(hod1);
            departmentRepository.save(itDept);
        }

        // User
        if (!userRepository.findByUsername("user1").isPresent()) {
            User user1 = new User();
            user1.setUsername("user1");
            user1.setEmail("user1@example.com");
            user1.setPassword(encodedPassword);
            user1.setRole(User.Role.USER);
            user1.setDepartment(itDept);
            user1.setEnabled(true);
            userRepository.save(user1);
        }

        // Approver
        if (!userRepository.findByUsername("approver1").isPresent()) {
            User approver1 = new User();
            approver1.setUsername("approver1");
            approver1.setEmail("approver1@example.com");
            approver1.setPassword(encodedPassword);
            approver1.setRole(User.Role.APPROVER);
            approver1.setApprovalLimit(new BigDecimal("100000"));
            approver1.setEnabled(true);
            userRepository.save(approver1);
        }

        // Buyer
        if (!userRepository.findByUsername("buyer1").isPresent()) {
            User buyer1 = new User();
            buyer1.setUsername("buyer1");
            buyer1.setEmail("buyer1@example.com");
            buyer1.setPassword(encodedPassword);
            buyer1.setRole(User.Role.BUYER);
            buyer1.setEnabled(true);
            userRepository.save(buyer1);
        }

        System.out.println("Test data initialized successfully!");
        System.out.println("Login credentials: username/password");
        System.out.println("hod1/Password1! - HOD");
        System.out.println("user1/Password1! - USER");
        System.out.println("approver1/Password1! - APPROVER");
        System.out.println("buyer1/Password1! - BUYER");
    }
}