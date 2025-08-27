package cicosy.templete.config;

import cicosy.templete.domain.Department;
import cicosy.templete.domain.Supplier;
import cicosy.templete.domain.User;
import cicosy.templete.repository.DepartmentRepository;
import cicosy.templete.repository.SupplierRepository;
import cicosy.templete.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedData(UserRepository users, DepartmentRepository departments,
                               SupplierRepository suppliers, PasswordEncoder encoder) {
        return args -> {
            if (users.count() == 0 && departments.count() == 0 && suppliers.count() == 0) {
                logger.info("Seeding data...");
                // Create Departments
                Department it = new Department();
                it.setName("IT");
                it.setBudgetLimit(new java.math.BigDecimal("50000"));
                departments.save(it);

                Department ops = new Department();
                ops.setName("Operations");
                ops.setBudgetLimit(new java.math.BigDecimal("30000"));
                departments.save(ops);

                // Create Users
                User hod = new User();
                hod.setUsername("hod1");
                hod.setEmail("hod1@example.com");
                String hodPassword = encoder.encode("Password1!");
                hod.setPassword(hodPassword);
                logger.info("Seeding HOD with encoded password: {}", hodPassword);
                hod.setRole(User.Role.HOD);
                hod.setDepartment(it);
                users.save(hod);

                it.setHod(hod);
                departments.save(it);

                User buyer = new User();
                buyer.setUsername("buyer1");
                buyer.setEmail("buyer1@example.com");
                String buyerPassword = encoder.encode("Password1!");
                buyer.setPassword(buyerPassword);
                logger.info("Seeding Buyer with encoded password: {}", buyerPassword);
                buyer.setRole(User.Role.BUYER);
                users.save(buyer);

                User approver = new User();
                approver.setUsername("approver1");
                approver.setEmail("approver1@example.com");
                String approverPassword = encoder.encode("Password1!");
                approver.setPassword(approverPassword);
                logger.info("Seeding Approver with encoded password: {}", approverPassword);
                approver.setRole(User.Role.APPROVER);
                users.save(approver);

                User regular = new User();
                regular.setUsername("user1");
                regular.setEmail("user1@example.com");
                String regularPassword = encoder.encode("Password1!");
                regular.setPassword(regularPassword);
                logger.info("Seeding User with encoded password: {}", regularPassword);
                regular.setRole(User.Role.USER);
                regular.setDepartment(it);
                users.save(regular);

                // Create Suppliers
                Supplier techSupplier = new Supplier();
                techSupplier.setName("Tech Solutions Inc.");
                techSupplier.setCategory("Technology");
                techSupplier.setContactInfo("contact@techsolutions.com");
                suppliers.save(techSupplier);

                Supplier officeSupplier = new Supplier();
                officeSupplier.setName("Office Supplies Co.");
                officeSupplier.setCategory("Stationery");
                officeSupplier.setContactInfo("sales@officesupplies.com");
                suppliers.save(officeSupplier);
            }
        };
    }
}
