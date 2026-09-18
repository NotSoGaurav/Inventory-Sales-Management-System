package com.inventory.service;

import com.inventory.enums.PaymentMethod;
import com.inventory.enums.StockStatus;
import com.inventory.enums.UserRole;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.model.*;
import com.inventory.repository.*;
import com.inventory.util.FileUtil;

import java.util.Arrays;

/**
 * Unit tests for business services (Product, Inventory, Customer, Auth, Sales).
 */
public class ServiceTest {

    public static int runTests() {
        System.out.println("\n--- Running Service Layer Tests ---");
        int passed = 0;

        String testUsers = "data/test_svc_users.txt";
        String testProds = "data/test_svc_prods.txt";
        String testCusts = "data/test_svc_custs.txt";
        String testSales = "data/test_svc_sales.txt";

        FileUtil.deleteFile(testUsers);
        FileUtil.deleteFile(testProds);
        FileUtil.deleteFile(testCusts);
        FileUtil.deleteFile(testSales);

        UserRepository userRepo = new UserRepository(testUsers);
        ProductRepository prodRepo = new ProductRepository(testProds);
        CustomerRepository custRepo = new CustomerRepository(testCusts);
        SaleRepository saleRepo = new SaleRepository(testSales);

        AuthenticationService authService = new AuthenticationService(userRepo);
        InventoryService invService = new InventoryService(prodRepo);
        CustomerService custService = new CustomerService(custRepo);
        ProductService prodService = new ProductService(prodRepo, null);
        SalesService salesService = new SalesService(saleRepo, prodRepo, custRepo, invService);

        // 1. AuthenticationService tests
        try {
            User user = authService.login("admin", "admin123");
            if (user != null && (authService.hasRole(UserRole.ADMIN) || user.getRole() == UserRole.ADMIN)) {
                passed++;
                System.out.println("  [PASS] AuthenticationService login & admin role verification");
            }
        } catch (ValidationException e) {
            System.err.println("  [FAIL] " + e.getMessage());
        }

        // 2. CustomerService tests
        try {
            Customer c = new Customer("C1", "Ramesh Kumar", "9876543210", "ramesh@example.com", "Bhopal");
            custService.addCustomer(c);
            if (custService.getCustomerById("C1").isPresent()) {
                passed++;
                System.out.println("  [PASS] CustomerService add & getById");
            }
        } catch (ValidationException e) {
            System.err.println("  [FAIL] " + e.getMessage());
        }

        // 3. ProductService & InventoryService tests
        try {
            Product p = new Product("P1", "Laptop", "Computers", 50000.0, 5, 2, "S1");
            prodService.addProduct(p);
            if (prodService.getProductById("P1") != null) {
                passed++;
                System.out.println("  [PASS] ProductService add & lookup");
            }

            // Increase stock
            invService.increaseStock("P1", 5);
            if (invService.getStock("P1").getQuantity() == 10) {
                passed++;
                System.out.println("  [PASS] InventoryService increaseStock (5 -> 10)");
            }

            // Decrease stock
            invService.decreaseStock("P1", 2);
            if (invService.getStock("P1").getQuantity() == 8) {
                passed++;
                System.out.println("  [PASS] InventoryService decreaseStock (10 -> 8)");
            }
        } catch (Exception e) {
            System.err.println("  [FAIL] " + e.getMessage());
        }

        // 4. SalesService checkout & inventory reduction
        try {
            SaleItem item = new SaleItem("P1", "Laptop", 3, 50000.0);
            Sale sale = new Sale("SALE-1", "C1", null, Arrays.asList(item), PaymentMethod.UPI);
            salesService.processSale(sale);

            // Stock should be 8 - 3 = 5
            if (invService.getStock("P1").getQuantity() == 5 && salesService.getTotalRevenue() == 150000.0) {
                passed++;
                System.out.println("  [PASS] SalesService processed sale, reduced stock to 5, revenue 150,000.0");
            }
        } catch (Exception e) {
            System.err.println("  [FAIL] " + e.getMessage());
        }

        // 5. Insufficient stock failure
        try {
            SaleItem hugeItem = new SaleItem("P1", "Laptop", 100, 50000.0);
            Sale failedSale = new Sale("SALE-2", "C1", null, Arrays.asList(hugeItem), PaymentMethod.CASH);
            salesService.processSale(failedSale);
            System.err.println("  [FAIL] Should fail due to insufficient stock");
        } catch (InsufficientStockException e) {
            passed++;
            System.out.println("  [PASS] InsufficientStockException thrown and caught correctly during checkout");
        } catch (Exception e) {
            System.err.println("  [FAIL] " + e.getMessage());
        }

        FileUtil.deleteFile(testUsers);
        FileUtil.deleteFile(testProds);
        FileUtil.deleteFile(testCusts);
        FileUtil.deleteFile(testSales);

        return passed;
    }
}
