package com.inventory.util;

import com.inventory.enums.PaymentMethod;
import com.inventory.enums.StockStatus;
import com.inventory.model.Customer;
import com.inventory.model.Product;
import com.inventory.model.Sale;
import com.inventory.model.SaleItem;
import com.inventory.model.Supplier;
import com.inventory.repository.CustomerRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SaleRepository;
import com.inventory.repository.SupplierRepository;
import com.inventory.service.CustomerService;
import com.inventory.service.InventoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SalesService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class SeedVerification {

    public static void main(String[] args) {
        System.out.println("=== Starting Seeding & Module Verification ===");

        // 1. Initial Seeding
        DataSeeder seeder = new DataSeeder();
        seeder.seedDemoData();

        SupplierRepository supplierRepo = new SupplierRepository();
        ProductRepository prodRepo = new ProductRepository();
        CustomerRepository custRepo = new CustomerRepository();
        SaleRepository saleRepo = new SaleRepository();

        ProductService productService = new ProductService(prodRepo, supplierRepo);
        CustomerService customerService = new CustomerService(custRepo);
        InventoryService inventoryService = new InventoryService(prodRepo);
        SalesService salesService = new SalesService(saleRepo, prodRepo, custRepo, inventoryService);

        // Verify Suppliers
        List<Supplier> suppliers = supplierRepo.findAll();
        System.out.println("Suppliers count: " + suppliers.size());
        for (Supplier s : suppliers) {
            System.out.println("  " + s.getSupplierId() + " - " + s.getName() + " - " + s.getEmail());
        }
        if (suppliers.size() < 2) {
            throw new RuntimeException("Expected at least 2 suppliers, found: " + suppliers.size());
        }

        // Verify Products
        List<Product> products = productService.getAllProducts();
        System.out.println("Products count: " + products.size());
        for (Product p : products) {
            System.out.printf("  %s - %s (%s) - Rs.%.1f - Stock: %d - Min: %d - Supplier: %s%n",
                    p.getProductId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity(), p.getMinStockThreshold(), p.getSupplierId());
        }
        if (products.size() != 10) {
            throw new RuntimeException("Expected exactly 10 demo products, found: " + products.size());
        }

        // Verify Customers
        List<Customer> customers = customerService.getAllCustomers();
        System.out.println("Customers count: " + customers.size());
        for (Customer c : customers) {
            System.out.printf("  %s - %s - %s - %s - %s%n",
                    c.getCustomerId(), c.getName(), c.getPhone(), c.getEmail(), c.getAddress());
        }
        if (customers.size() != 4) {
            throw new RuntimeException("Expected exactly 4 demo customers, found: " + customers.size());
        }

        // Verify Product Search
        List<Product> searchByName = productService.searchProducts("Ball Pen");
        if (searchByName.isEmpty() || !searchByName.get(0).getProductId().equals("P001")) {
            throw new RuntimeException("Product search by name failed");
        }
        List<Product> searchByCat = productService.getProductsByCategory("Electronics");
        if (searchByCat.size() != 5) {
            throw new RuntimeException("Expected 5 electronics products, found: " + searchByCat.size());
        }
        System.out.println("[PASS] Product search verified.");

        // Verify Customer Search
        List<Customer> custSearch = customerService.searchCustomers("Rahul");
        if (custSearch.isEmpty() || !custSearch.get(0).getCustomerId().equals("C001")) {
            throw new RuntimeException("Customer search failed");
        }
        System.out.println("[PASS] Customer search verified.");

        // Verify Inventory Status
        StockStatus statusP001 = prodRepo.findById("P001").get().getStockStatus();
        if (statusP001 != StockStatus.IN_STOCK) {
            throw new RuntimeException("P001 should be IN_STOCK");
        }
        System.out.println("[PASS] Inventory status verified.");

        // Verify Duplicate Prevention by running seeder multiple times
        System.out.println("Testing duplicate prevention on subsequent launches...");
        seeder.seedDemoData();
        seeder.seedDemoData();
        if (supplierRepo.findAll().size() != suppliers.size()) {
            throw new RuntimeException("Supplier count changed on re-seed!");
        }
        if (productService.getAllProducts().size() != 10) {
            throw new RuntimeException("Product count changed on re-seed!");
        }
        if (customerService.getAllCustomers().size() != 4) {
            throw new RuntimeException("Customer count changed on re-seed!");
        }
        System.out.println("[PASS] Duplicate prevention verified: 0 duplicates created on repeated executions.");

        // Test Sale
        try {
            int stockBefore = prodRepo.findById("P001").get().getQuantity();
            SaleItem item = salesService.createSaleItem("P001", 5);
            Sale sale = new Sale("SALE-DEMO1", "C001", LocalDateTime.now(), Arrays.asList(item), PaymentMethod.CASH);
            salesService.processSale(sale);
            int stockAfter = prodRepo.findById("P001").get().getQuantity();
            System.out.println("Sale completed! Stock before: " + stockBefore + ", Stock after: " + stockAfter);
            if (stockAfter != stockBefore - 5) {
                throw new RuntimeException("Stock did not decrease properly");
            }
            if (salesService.getTotalRevenue() < 50.0) {
                throw new RuntimeException("Revenue not tracked properly");
            }

            // Clean up test sale and restore stock for clean demo state
            inventoryService.increaseStock("P001", 5);
            saleRepo.deleteById("SALE-DEMO1");
            System.out.println("[PASS] Test sale verified and stock restored.");
        } catch (Exception e) {
            throw new RuntimeException("Sale verification failed: " + e.getMessage(), e);
        }

        System.out.println("\n=== ALL VERIFICATION CHECKS PASSED SUCCESSFULLY ===");
    }
}
