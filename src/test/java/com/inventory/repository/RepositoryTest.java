package com.inventory.repository;

import com.inventory.enums.PaymentMethod;
import com.inventory.enums.UserRole;
import com.inventory.model.*;
import com.inventory.util.FileUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for repository CRUD and file persistence operations.
 */
public class RepositoryTest {

    public static int runTests() {
        System.out.println("\n--- Running Repository Tests ---");
        int passed = 0;

        String testUsers = "data/test_repo_users.txt";
        String testProds = "data/test_repo_prods.txt";
        String testSales = "data/test_repo_sales.txt";

        FileUtil.deleteFile(testUsers);
        FileUtil.deleteFile(testProds);
        FileUtil.deleteFile(testSales);

        // 1. UserRepository CRUD
        UserRepository userRepo = new UserRepository(testUsers);
        User u = new User("U10", "johndoe", "pass123", "John Doe", UserRole.CASHIER);
        if (userRepo.save(u) && userRepo.findById("U10").isPresent()) {
            passed++;
            System.out.println("  [PASS] UserRepository save & findById");
        }
        u.setFullName("John Updated");
        if (userRepo.update(u) && userRepo.findById("U10").get().getFullName().equals("John Updated")) {
            passed++;
            System.out.println("  [PASS] UserRepository update");
        }
        if (userRepo.deleteById("U10") && !userRepo.findById("U10").isPresent()) {
            passed++;
            System.out.println("  [PASS] UserRepository deleteById");
        }

        // 2. ProductRepository CRUD
        ProductRepository prodRepo = new ProductRepository(testProds);
        Product p = new Product("P10", "Tablet", "Electronics", 15000.0, 5, 2, "S1");
        if (prodRepo.save(p) && prodRepo.findById("P10").isPresent()) {
            passed++;
            System.out.println("  [PASS] ProductRepository save & findById");
        }
        if (prodRepo.findByCategory("Electronics").size() == 1) {
            passed++;
            System.out.println("  [PASS] ProductRepository findByCategory");
        }

        // 3. SaleRepository with Composite Items
        SaleRepository saleRepo = new SaleRepository(testSales);
        SaleItem item1 = new SaleItem("P10", "Tablet", 2, 15000.0);
        Sale sale = new Sale("SALE-99", "C1", LocalDateTime.now(), Arrays.asList(item1), PaymentMethod.UPI);
        if (saleRepo.save(sale)) {
            Optional<Sale> opt = saleRepo.findById("SALE-99");
            if (opt.isPresent() && opt.get().getItems().size() == 1 && opt.get().getTotalAmount() == 30000.0) {
                passed++;
                System.out.println("  [PASS] SaleRepository composite serialization & total amount");
            }
        }

        FileUtil.deleteFile(testUsers);
        FileUtil.deleteFile(testProds);
        FileUtil.deleteFile(testSales);

        return passed;
    }
}
