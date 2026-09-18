package com.inventory.model;

import com.inventory.enums.PaymentMethod;
import com.inventory.enums.StockStatus;
import com.inventory.enums.UserRole;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Unit tests for domain entity calculations and model behaviors.
 */
public class ModelTest {

    public static int runTests() {
        System.out.println("\n--- Running Model Tests ---");
        int passed = 0;

        // 1. User creation and toString
        User user = new User("U1", "admin", "admin123", "Admin User", UserRole.ADMIN);
        if (user.getRole() == UserRole.ADMIN && user.getUsername().equals("admin")) {
            System.out.println("  [PASS] User entity fields and role");
            passed++;
        }

        // 2. Product stock status calculations
        Product inStockProd = new Product("P1", "Widget", "General", 100.0, 20, 5, "S1");
        Product lowStockProd = new Product("P2", "Gadget", "General", 150.0, 4, 5, "S1");
        Product outStockProd = new Product("P3", "Doohickey", "General", 200.0, 0, 5, "S1");

        if (inStockProd.getStockStatus() == StockStatus.IN_STOCK) {
            System.out.println("  [PASS] Product stock status IN_STOCK");
            passed++;
        }
        if (lowStockProd.getStockStatus() == StockStatus.LOW_STOCK) {
            System.out.println("  [PASS] Product stock status LOW_STOCK");
            passed++;
        }
        if (outStockProd.getStockStatus() == StockStatus.OUT_OF_STOCK) {
            System.out.println("  [PASS] Product stock status OUT_OF_STOCK");
            passed++;
        }

        // 3. SaleItem subtotal calculation
        SaleItem item1 = new SaleItem("P1", "Widget", 3, 100.0);
        if (item1.getSubtotal() == 300.0) {
            System.out.println("  [PASS] SaleItem subtotal (3 * 100.0 = 300.0)");
            passed++;
        }

        // 4. Sale total calculation and dynamic item addition/removal
        SaleItem item2 = new SaleItem("P2", "Gadget", 2, 150.0);
        Sale sale = new Sale("SALE-1", "C1", LocalDateTime.now(), Arrays.asList(item1, item2), PaymentMethod.CASH);
        if (sale.getTotalAmount() == 600.0) {
            System.out.println("  [PASS] Sale total amount calculation (300 + 300 = 600.0)");
            passed++;
        }

        sale.removeItem(item2);
        if (sale.getTotalAmount() == 300.0 && sale.getItems().size() == 1) {
            System.out.println("  [PASS] Sale dynamic recalculation upon item removal (total = 300.0)");
            passed++;
        }

        return passed;
    }
}
