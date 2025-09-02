package cicosy.templete.config;

import cicosy.templete.domain.CatalogueItem;
import cicosy.templete.domain.Department;
import cicosy.templete.domain.Supplier;
import cicosy.templete.domain.User;
import cicosy.templete.repository.CatalogueItemRepository;
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
                               SupplierRepository suppliers, CatalogueItemRepository catalogueItems, PasswordEncoder encoder) {
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

                // Create Catalogue Items
                CatalogueItem laptop = new CatalogueItem();
                laptop.setName("Dell Laptop");
                laptop.setDescription("Dell Inspiron 15 3000 Series");
                laptop.setCategory("Electronics");
                laptop.setPrice(new java.math.BigDecimal("45000"));
                laptop.setSpecifications("Intel i5, 8GB RAM, 256GB SSD");
                catalogueItems.save(laptop);

                CatalogueItem mouse = new CatalogueItem();
                mouse.setName("Wireless Mouse");
                mouse.setDescription("Logitech Wireless Mouse");
                mouse.setCategory("Electronics");
                mouse.setPrice(new java.math.BigDecimal("1500"));
                mouse.setSpecifications("2.4GHz wireless, 1000 DPI");
                catalogueItems.save(mouse);

                CatalogueItem chair = new CatalogueItem();
                chair.setName("Office Chair");
                chair.setDescription("Ergonomic Office Chair");
                chair.setCategory("Furniture");
                chair.setPrice(new java.math.BigDecimal("8000"));
                chair.setSpecifications("Adjustable height, lumbar support");
                catalogueItems.save(chair);

                CatalogueItem pen = new CatalogueItem();
                pen.setName("Ball Point Pen");
                pen.setDescription("Blue ink ball point pen");
                pen.setCategory("Stationery");
                pen.setPrice(new java.math.BigDecimal("10"));
                pen.setSpecifications("0.7mm tip, blue ink");
                catalogueItems.save(pen);

                CatalogueItem notebook = new CatalogueItem();
                notebook.setName("Spiral Notebook");
                notebook.setDescription("A4 size spiral notebook");
                notebook.setCategory("Stationery");
                notebook.setPrice(new java.math.BigDecimal("50"));
                notebook.setSpecifications("200 pages, ruled");
                catalogueItems.save(notebook);
            }
        };
    }
}
