package cicosy.templete.config;

import cicosy.templete.domain.Department;
import cicosy.templete.domain.User;
import cicosy.templete.repository.DepartmentRepository;
import cicosy.templete.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedUsers(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.count() == 0) {
                User hod = new User();
                hod.setUsername("hod1");
                hod.setEmail("hod1@example.com");
                hod.setPassword(encoder.encode("Password1!"));
                hod.setRole(User.Role.HOD);
                users.save(hod);

                User buyer = new User();
                buyer.setUsername("buyer1");
                buyer.setEmail("buyer1@example.com");
                buyer.setPassword(encoder.encode("Password1!"));
                buyer.setRole(User.Role.BUYER);
                users.save(buyer);

                User approver = new User();
                approver.setUsername("approver1");
                approver.setEmail("approver1@example.com");
                approver.setPassword(encoder.encode("Password1!"));
                approver.setRole(User.Role.APPROVER);
                users.save(approver);

                User regular = new User();
                regular.setUsername("user1");
                regular.setEmail("user1@example.com");
                regular.setPassword(encoder.encode("Password1!"));
                regular.setRole(User.Role.USER);
                users.save(regular);
            }
        };
    }

    @Bean
    CommandLineRunner seedDepartments(DepartmentRepository departments) {
        return args -> {
            if (departments.count() == 0) {
                Department it = new Department();
                it.setName("IT");
                it.setBudgetLimit(new java.math.BigDecimal("50000"));
                departments.save(it);

                Department ops = new Department();
                ops.setName("Operations");
                ops.setBudgetLimit(new java.math.BigDecimal("30000"));
                departments.save(ops);
            }
        };
    }
}


