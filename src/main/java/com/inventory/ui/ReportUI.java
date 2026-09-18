package com.inventory.ui;

import com.inventory.enums.PaymentMethod;
import com.inventory.model.Customer;
import com.inventory.model.Product;
import com.inventory.model.Sale;
import com.inventory.service.CustomerService;
import com.inventory.service.InventoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SalesService;

import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console UI layer for Business Reports, Stock Analytics, and Revenue Summaries.
 */
public class ReportUI {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final CustomerService customerService;
    private final SalesService salesService;
    private final Scanner scanner;

    public ReportUI() {
        this(new ProductService(), new InventoryService(), new CustomerService(), new SalesService(), new Scanner(System.in));
    }

    public ReportUI(ProductService productService, InventoryService inventoryService,
                    CustomerService customerService, SalesService salesService, Scanner scanner) {
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.customerService = customerService;
        this.salesService = salesService;
        this.scanner = scanner;
    }

    /**
     * Displays the Reports & Analytics menu loop.
     */
    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n==================================================");
            System.out.println("               REPORTS & ANALYTICS                ");
            System.out.println("==================================================");
            System.out.println(" 1. System Executive Dashboard Summary");
            System.out.println(" 2. Inventory & Stock Status Report");
            System.out.println(" 3. Low-Stock & Reorder Alert Report");
            System.out.println(" 4. Sales History & Transaction Log");
            System.out.println(" 5. Revenue by Payment Method Breakdown");
            System.out.println(" 6. Customer Purchase Activity Summary");
            System.out.println(" 7. Back to Main Menu");
            System.out.println("==================================================");
            System.out.print("Enter your choice (1-7): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayExecutiveSummary();
                    break;
                case "2":
                    displayInventoryReport();
                    break;
                case "3":
                    displayLowStockReport();
                    break;
                case "4":
                    displaySalesHistory();
                    break;
                case "5":
                    displayPaymentMethodBreakdown();
                    break;
                case "6":
                    displayCustomerActivity();
                    break;
                case "7":
                    running = false;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("[Error] Invalid option. Please enter a number between 1 and 7.");
            }
        }
    }

    private void displayExecutiveSummary() {
        List<Product> products = productService.getAllProducts();
        List<Customer> customers = customerService.getAllCustomers();
        List<Sale> sales = salesService.getAllSales();
        List<Product> lowStock = inventoryService.getLowStockProducts();
        List<Product> outOfStock = inventoryService.getOutOfStockProducts();

        double totalRevenue = salesService.getTotalRevenue();
        int totalUnitsInStock = 0;
        double inventoryValuation = 0.0;
        for (Product p : products) {
            totalUnitsInStock += p.getQuantity();
            inventoryValuation += (p.getQuantity() * p.getPrice());
        }

        System.out.println("\n==========================================================");
        System.out.println("           EXECUTIVE SYSTEM DASHBOARD SUMMARY             ");
        System.out.println("==========================================================");
        System.out.printf(" Total Products Cataloged     : %d%n", products.size());
        System.out.printf(" Total Registered Customers   : %d%n", customers.size());
        System.out.printf(" Total Units in Inventory     : %d units%n", totalUnitsInStock);
        System.out.printf(" Total Inventory Asset Value  : Rs. %.2f%n", inventoryValuation);
        System.out.printf(" Total Completed Sales        : %d transactions%n", sales.size());
        System.out.printf(" Cumulative Gross Revenue     : Rs. %.2f%n", totalRevenue);
        System.out.println("----------------------------------------------------------");
        System.out.printf(" [!] Low Stock Items          : %d%n", lowStock.size());
        System.out.printf(" [X] Out of Stock Items       : %d%n", outOfStock.size());
        System.out.println("==========================================================");
    }

    private void displayInventoryReport() {
        System.out.println("\n--- Comprehensive Inventory Status Report ---");
        List<Product> products = inventoryService.getAllInventory();

        if (products.isEmpty()) {
            System.out.println("No inventory records found.");
            return;
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-25s | %-15s | %-10s | %-8s | %-12s | %-12s%n",
                "ID", "Name", "Category", "Price", "Qty", "Stock Value", "Status");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        double totalValue = 0.0;
        int totalQty = 0;

        for (Product p : products) {
            double value = p.getQuantity() * p.getPrice();
            totalValue += value;
            totalQty += p.getQuantity();

            System.out.printf("%-10s | %-25s | %-15s | %-10.2f | %-8d | Rs. %-8.2f | %-12s%n",
                    p.getProductId(),
                    truncate(p.getName(), 25),
                    truncate(p.getCategory(), 15),
                    p.getPrice(),
                    p.getQuantity(),
                    value,
                    p.getStockStatus());
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("Summary: %d Products | Total Units: %d | Total Inventory Valuation: Rs. %.2f%n",
                products.size(), totalQty, totalValue);
    }

    private void displayLowStockReport() {
        System.out.println("\n--- Low Stock and Reorder Alerts ---");
        List<Product> lowStock = inventoryService.getLowStockProducts();
        List<Product> outOfStock = inventoryService.getOutOfStockProducts();

        if (lowStock.isEmpty() && outOfStock.isEmpty()) {
            System.out.println("[Status Normal] All product inventory levels are above their minimum thresholds.");
            return;
        }

        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-25s | %-10s | %-12s | %-12s | %-12s%n",
                "ID", "Product Name", "Available", "Min Threshold", "Status", "Supplier ID");
        System.out.println("-------------------------------------------------------------------------------------------------");

        for (Product p : outOfStock) {
            System.out.printf("%-10s | %-25s | %-10d | %-12d | %-12s | %-12s%n",
                    p.getProductId(), truncate(p.getName(), 25), p.getQuantity(), p.getMinStockThreshold(),
                    p.getStockStatus(), p.getSupplierId() != null ? p.getSupplierId() : "-");
        }
        for (Product p : lowStock) {
            System.out.printf("%-10s | %-25s | %-10d | %-12d | %-12s | %-12s%n",
                    p.getProductId(), truncate(p.getName(), 25), p.getQuantity(), p.getMinStockThreshold(),
                    p.getStockStatus(), p.getSupplierId() != null ? p.getSupplierId() : "-");
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("Total Action Required: %d items requiring reorder / restocking.%n",
                (lowStock.size() + outOfStock.size()));
    }

    private void displaySalesHistory() {
        System.out.println("\n--- Historical Sales Transactions Log ---");
        List<Sale> sales = salesService.getAllSales();

        if (sales.isEmpty()) {
            System.out.println("No sales transactions have been recorded yet.");
            return;
        }

        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-12s | %-18s | %-10s | %-12s | %-8s%n",
                "Sale ID", "Customer ID", "Timestamp", "Payment", "Total Amount", "Item Count");
        System.out.println("--------------------------------------------------------------------------------------------------");

        for (Sale s : sales) {
            String dateStr = s.getSaleDate() != null ? s.getSaleDate().format(DATE_FORMAT) : "-";
            System.out.printf("%-12s | %-12s | %-18s | %-10s | Rs. %-8.2f | %-8d%n",
                    s.getSaleId(), s.getCustomerId(), dateStr, s.getPaymentMethod(),
                    s.getTotalAmount(), s.getItems() != null ? s.getItems().size() : 0);
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("Total Transactions: %d | Cumulative Revenue: Rs. %.2f%n",
                sales.size(), salesService.getTotalRevenue());
    }

    private void displayPaymentMethodBreakdown() {
        System.out.println("\n--- Sales by Payment Method Breakdown ---");
        List<Sale> sales = salesService.getAllSales();

        if (sales.isEmpty()) {
            System.out.println("No sales recorded yet.");
            return;
        }

        Map<PaymentMethod, Double> revenueByMethod = new EnumMap<>(PaymentMethod.class);
        Map<PaymentMethod, Integer> countByMethod = new EnumMap<>(PaymentMethod.class);

        for (PaymentMethod m : PaymentMethod.values()) {
            revenueByMethod.put(m, 0.0);
            countByMethod.put(m, 0);
        }

        double totalRevenue = 0.0;
        for (Sale s : sales) {
            PaymentMethod pm = s.getPaymentMethod() != null ? s.getPaymentMethod() : PaymentMethod.CASH;
            revenueByMethod.put(pm, revenueByMethod.get(pm) + s.getTotalAmount());
            countByMethod.put(pm, countByMethod.get(pm) + 1);
            totalRevenue += s.getTotalAmount();
        }

        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-15s | %-15s | %-15s%n",
                "Payment Method", "Transactions", "Total Revenue", "% Share");
        System.out.println("----------------------------------------------------------------------------------");

        for (PaymentMethod m : PaymentMethod.values()) {
            double rev = revenueByMethod.get(m);
            int count = countByMethod.get(m);
            double share = totalRevenue > 0 ? (rev / totalRevenue) * 100.0 : 0.0;

            System.out.printf("%-15s | %-15d | Rs. %-11.2f | %-6.1f%%%n",
                    m.name(), count, rev, share);
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("Total: %d Transactions | Rs. %.2f Revenue%n", sales.size(), totalRevenue);
    }

    private void displayCustomerActivity() {
        System.out.println("\n--- Customer Purchase Activity Summary ---");
        List<Customer> customers = customerService.getAllCustomers();
        List<Sale> sales = salesService.getAllSales();

        if (customers.isEmpty()) {
            System.out.println("No customers registered yet.");
            return;
        }

        // Map customer ID to count and spend
        Map<String, Integer> orderCounts = new HashMap<>();
        Map<String, Double> totalSpends = new HashMap<>();

        for (Sale s : sales) {
            String cid = s.getCustomerId();
            if (cid != null) {
                orderCounts.put(cid, orderCounts.getOrDefault(cid, 0) + 1);
                totalSpends.put(cid, totalSpends.getOrDefault(cid, 0.0) + s.getTotalAmount());
            }
        }

        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-20s | %-15s | %-12s | %-15s%n",
                "ID", "Customer Name", "Phone", "Total Orders", "Total Spent");
        System.out.println("--------------------------------------------------------------------------------------------------");

        for (Customer c : customers) {
            int orders = orderCounts.getOrDefault(c.getCustomerId(), 0);
            double spent = totalSpends.getOrDefault(c.getCustomerId(), 0.0);

            System.out.printf("%-10s | %-20s | %-15s | %-12d | Rs. %-11.2f%n",
                    c.getCustomerId(),
                    truncate(c.getName(), 20),
                    c.getPhone(),
                    orders,
                    spent);
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
