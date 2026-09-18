package com.inventory.ui;

import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.model.Product;
import com.inventory.service.InventoryService;
import com.inventory.util.InputValidator;

import java.util.List;
import java.util.Scanner;

/**
 * Console UI layer for Inventory & Stock Management.
 * Handles inventory level viewing, stock adjustments, and low-stock alerts.
 */
public class InventoryUI {

    private final InventoryService inventoryService;
    private final Scanner scanner;

    public InventoryUI() {
        this(new InventoryService(), new Scanner(System.in));
    }

    public InventoryUI(InventoryService inventoryService, Scanner scanner) {
        this.inventoryService = inventoryService;
        this.scanner = scanner;
    }

    /**
     * Displays the Inventory Management menu loop.
     */
    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n==================================================");
            System.out.println("              INVENTORY MANAGEMENT                ");
            System.out.println("==================================================");
            System.out.println(" 1. View Current Inventory Stock");
            System.out.println(" 2. Check Product Stock by ID");
            System.out.println(" 3. Increase Stock (Restock / Goods Received)");
            System.out.println(" 4. Decrease Stock (Manual Adjustment / Damage)");
            System.out.println(" 5. Set / Audit Stock Quantity");
            System.out.println(" 6. View Low-Stock Alerts");
            System.out.println(" 7. View Out-of-Stock Products");
            System.out.println(" 8. Back to Main Menu");
            System.out.println("==================================================");
            System.out.print("Enter your choice (1-8): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleViewInventory();
                    break;
                case "2":
                    handleCheckStock();
                    break;
                case "3":
                    handleIncreaseStock();
                    break;
                case "4":
                    handleDecreaseStock();
                    break;
                case "5":
                    handleSetStockQuantity();
                    break;
                case "6":
                    handleViewLowStock();
                    break;
                case "7":
                    handleViewOutOfStock();
                    break;
                case "8":
                    running = false;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("[Error] Invalid option. Please enter a number between 1 and 8.");
            }
        }
    }

    private void handleViewInventory() {
        System.out.println("\n--- Current Inventory Levels ---");
        List<Product> products = inventoryService.getAllInventory();
        displayInventoryTable(products);
    }

    private void handleCheckStock() {
        System.out.println("\n--- Check Stock by Product ID ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();

        try {
            Product product = inventoryService.getStock(id);
            displayStockCard(product);
        } catch (ProductNotFoundException e) {
            System.out.println("[Not Found] " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void handleIncreaseStock() {
        System.out.println("\n--- Increase Stock (Restock) ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();

        try {
            Product existing = inventoryService.getStock(id);
            System.out.println("Product: " + existing.getName() + " | Current Stock: " + existing.getQuantity());

            System.out.print("Enter quantity to add: ");
            String qtyInput = scanner.nextLine().trim();
            int amount = InputValidator.parsePositiveInt(qtyInput, "Increase quantity");

            Product updated = inventoryService.increaseStock(id, amount);
            System.out.println("[Success] Stock updated! Previous: " + existing.getQuantity() +
                    " -> New Stock: " + updated.getQuantity() + " (" + updated.getStockStatus() + ")");
        } catch (ProductNotFoundException e) {
            System.out.println("[Not Found] " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void handleDecreaseStock() {
        System.out.println("\n--- Decrease Stock (Adjustment) ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();

        try {
            Product existing = inventoryService.getStock(id);
            System.out.println("Product: " + existing.getName() + " | Available Stock: " + existing.getQuantity());

            System.out.print("Enter quantity to deduct: ");
            String qtyInput = scanner.nextLine().trim();
            int amount = InputValidator.parsePositiveInt(qtyInput, "Deduction quantity");

            Product updated = inventoryService.decreaseStock(id, amount);
            System.out.println("[Success] Stock deducted! Previous: " + existing.getQuantity() +
                    " -> Remaining Stock: " + updated.getQuantity() + " (" + updated.getStockStatus() + ")");
        } catch (InsufficientStockException e) {
            System.out.println("[Insufficient Stock Error] " + e.getMessage());
        } catch (ProductNotFoundException e) {
            System.out.println("[Not Found] " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void handleSetStockQuantity() {
        System.out.println("\n--- Set / Audit Stock Quantity ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();

        try {
            Product existing = inventoryService.getStock(id);
            System.out.println("Product: " + existing.getName() + " | Current Recorded Stock: " + existing.getQuantity());

            System.out.print("Enter actual counted stock quantity (>= 0): ");
            String qtyInput = scanner.nextLine().trim();
            int newQty = InputValidator.parseNonNegativeInt(qtyInput, "Audited stock quantity");

            Product updated = inventoryService.updateStock(id, newQty);
            System.out.println("[Success] Stock updated to " + updated.getQuantity() + " (" + updated.getStockStatus() + ").");
        } catch (ProductNotFoundException e) {
            System.out.println("[Not Found] " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void handleViewLowStock() {
        System.out.println("\n==================================================");
        System.out.println("               [ALERT] LOW STOCK PRODUCTS         ");
        System.out.println("==================================================");
        List<Product> lowStock = inventoryService.getLowStockProducts();
        if (lowStock.isEmpty()) {
            System.out.println("No low-stock products at this time.");
        } else {
            displayInventoryTable(lowStock);
        }
    }

    private void handleViewOutOfStock() {
        System.out.println("\n==================================================");
        System.out.println("             [CRITICAL] OUT OF STOCK PRODUCTS     ");
        System.out.println("==================================================");
        List<Product> outOfStock = inventoryService.getOutOfStockProducts();
        if (outOfStock.isEmpty()) {
            System.out.println("No out-of-stock products at this time.");
        } else {
            displayInventoryTable(outOfStock);
        }
    }

    private void displayInventoryTable(List<Product> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("No inventory records found.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-25s | %-12s | %-12s | %-15s%n",
                "ID", "Name", "Stock Qty", "Min Reorder", "Status");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (Product p : products) {
            System.out.printf("%-10s | %-25s | %-12d | %-12d | %-15s%n",
                    p.getProductId(),
                    truncate(p.getName(), 25),
                    p.getQuantity(),
                    p.getMinStockThreshold(),
                    p.getStockStatus());
        }
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println("Total items: " + products.size());
    }

    private void displayStockCard(Product p) {
        System.out.println("\n==========================================");
        System.out.println("           STOCK INFORMATION              ");
        System.out.println("==========================================");
        System.out.println(" Product ID     : " + p.getProductId());
        System.out.println(" Product Name   : " + p.getName());
        System.out.println(" Category       : " + p.getCategory());
        System.out.println(" Available Qty  : " + p.getQuantity());
        System.out.println(" Min Threshold  : " + p.getMinStockThreshold());
        System.out.println(" Stock Status   : " + p.getStockStatus());
        System.out.println("==========================================");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
