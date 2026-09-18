package com.inventory.ui;

import com.inventory.enums.PaymentMethod;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.model.Sale;
import com.inventory.model.SaleItem;
import com.inventory.service.SalesService;
import com.inventory.util.InputValidator;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console UI layer for Sales and Billing operations.
 * Handles the complete checkout workflow, cart item entry, payment selection, and invoice printing.
 */
public class SalesUI {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SalesService salesService;
    private final Scanner scanner;

    public SalesUI() {
        this(new SalesService(), new Scanner(System.in));
    }

    public SalesUI(SalesService salesService, Scanner scanner) {
        this.salesService = salesService;
        this.scanner = scanner;
    }

    /**
     * Displays the Sales & Billing menu loop.
     */
    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n==================================================");
            System.out.println("                 SALES & BILLING                  ");
            System.out.println("==================================================");
            System.out.println(" 1. Create New Sale / Checkout");
            System.out.println(" 2. View All Sales Transactions");
            System.out.println(" 3. Find Sale by ID / Reprint Invoice");
            System.out.println(" 4. View Sales by Customer ID");
            System.out.println(" 5. Back to Main Menu");
            System.out.println("==================================================");
            System.out.print("Enter your choice (1-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleCreateSale();
                    break;
                case "2":
                    handleViewAllSales();
                    break;
                case "3":
                    handleFindSaleById();
                    break;
                case "4":
                    handleViewSalesByCustomer();
                    break;
                case "5":
                    running = false;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("[Error] Invalid option. Please enter a number between 1 and 5.");
            }
        }
    }

    private void handleCreateSale() {
        System.out.println("\n==================================================");
        System.out.println("                 NEW SALE CHECKOUT                ");
        System.out.println("==================================================");

        try {
            String defaultSaleId = salesService.generateSaleId();
            System.out.print("Sale ID [" + defaultSaleId + "]: ");
            String saleIdInput = scanner.nextLine().trim();
            String saleId = saleIdInput.isEmpty() ? defaultSaleId : saleIdInput;

            System.out.print("Enter Customer ID: ");
            String customerId = scanner.nextLine().trim();

            List<SaleItem> cartItems = new ArrayList<>();
            boolean addingItems = true;

            while (addingItems) {
                System.out.println("\n--- Add Item to Cart ---");
                System.out.print("Enter Product ID: ");
                String productId = scanner.nextLine().trim();

                System.out.print("Enter Quantity: ");
                String qtyInput = scanner.nextLine().trim();
                int quantity = InputValidator.parsePositiveInt(qtyInput, "Quantity");

                try {
                    SaleItem item = salesService.createSaleItem(productId, quantity);
                    cartItems.add(item);
                    System.out.println("[Added] " + item.getProductName() + " x " + item.getQuantity() +
                            " @ Rs. " + item.getUnitPrice() + " (Subtotal: Rs. " + item.getSubtotal() + ")");
                } catch (ProductNotFoundException e) {
                    System.out.println("[Not Found] " + e.getMessage());
                } catch (InsufficientStockException e) {
                    System.out.println("[Insufficient Stock] " + e.getMessage());
                } catch (ValidationException e) {
                    System.out.println("[Validation Error] " + e.getMessage());
                }

                // Display running cart summary
                double runningTotal = 0.0;
                for (SaleItem it : cartItems) {
                    runningTotal += it.getSubtotal();
                }
                System.out.printf("Current Cart Total: Rs. %.2f (%d item(s))%n", runningTotal, cartItems.size());

                System.out.print("Add another product? (y/n): ");
                String more = scanner.nextLine().trim();
                if (!more.equalsIgnoreCase("y") && !more.equalsIgnoreCase("yes")) {
                    addingItems = false;
                }
            }

            if (cartItems.isEmpty()) {
                System.out.println("[Cancelled] No items were added to the sale. Transaction cancelled.");
                return;
            }

            // Select payment method
            System.out.println("\nSelect Payment Method:");
            System.out.println(" 1. CASH");
            System.out.println(" 2. CARD");
            System.out.println(" 3. UPI");
            System.out.print("Enter payment choice (1-3): ");
            String payChoice = scanner.nextLine().trim();

            PaymentMethod paymentMethod;
            switch (payChoice) {
                case "2":
                    paymentMethod = PaymentMethod.CARD;
                    break;
                case "3":
                    paymentMethod = PaymentMethod.UPI;
                    break;
                case "1":
                default:
                    paymentMethod = PaymentMethod.CASH;
                    break;
            }

            Sale sale = new Sale(saleId, customerId, null, cartItems, paymentMethod);
            Sale completedSale = salesService.processSale(sale);

            System.out.println("\n[Success] Sale processed and inventory updated successfully!");
            printInvoice(completedSale);

        } catch (InsufficientStockException e) {
            System.out.println("[Checkout Failed] " + e.getMessage());
        } catch (ProductNotFoundException e) {
            System.out.println("[Checkout Failed] " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Error] Checkout failed unexpectedly: " + e.getMessage());
        }
    }

    private void handleViewAllSales() {
        System.out.println("\n--- All Sales Transactions ---");
        List<Sale> sales = salesService.getAllSales();

        if (sales.isEmpty()) {
            System.out.println("No sales transactions found.");
            return;
        }

        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-12s | %-18s | %-10s | %-12s | %-8s%n",
                "Sale ID", "Customer ID", "Date & Time", "Payment", "Total Amount", "Items");
        System.out.println("--------------------------------------------------------------------------------------------------");

        for (Sale s : sales) {
            String dateStr = s.getSaleDate() != null ? s.getSaleDate().format(DATE_FORMAT) : "-";
            System.out.printf("%-12s | %-12s | %-18s | %-10s | Rs. %-8.2f | %-8d%n",
                    s.getSaleId(),
                    s.getCustomerId(),
                    dateStr,
                    s.getPaymentMethod(),
                    s.getTotalAmount(),
                    s.getItems() != null ? s.getItems().size() : 0);
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("Total Recorded Sales: %d | Cumulative Revenue: Rs. %.2f%n", sales.size(), salesService.getTotalRevenue());
    }

    private void handleFindSaleById() {
        System.out.println("\n--- Find Sale / Reprint Invoice ---");
        System.out.print("Enter Sale ID: ");
        String id = scanner.nextLine().trim();

        try {
            Optional<Sale> opt = salesService.getSaleById(id);
            if (opt.isPresent()) {
                printInvoice(opt.get());
            } else {
                System.out.println("[Not Found] Sale with ID '" + id + "' was not found.");
            }
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void handleViewSalesByCustomer() {
        System.out.println("\n--- Sales by Customer ID ---");
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine().trim();

        List<Sale> sales = salesService.getSalesByCustomer(customerId);
        if (sales.isEmpty()) {
            System.out.println("No sales transactions found for customer ID: " + customerId);
            return;
        }

        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-18s | %-10s | %-12s | %-8s%n",
                "Sale ID", "Date & Time", "Payment", "Total Amount", "Items");
        System.out.println("--------------------------------------------------------------------------------------------------");

        double totalCustomerSpend = 0.0;
        for (Sale s : sales) {
            String dateStr = s.getSaleDate() != null ? s.getSaleDate().format(DATE_FORMAT) : "-";
            System.out.printf("%-12s | %-18s | %-10s | Rs. %-8.2f | %-8d%n",
                    s.getSaleId(),
                    dateStr,
                    s.getPaymentMethod(),
                    s.getTotalAmount(),
                    s.getItems() != null ? s.getItems().size() : 0);
            totalCustomerSpend += s.getTotalAmount();
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("Transactions: %d | Total Spent: Rs. %.2f%n", sales.size(), totalCustomerSpend);
    }

    /**
     * Prints a formatted ASCII sales invoice / bill.
     *
     * @param sale the Sale to display
     */
    public void printInvoice(Sale sale) {
        String dateStr = sale.getSaleDate() != null ? sale.getSaleDate().format(DATE_FORMAT) : "-";

        System.out.println("\n=========================================================");
        System.out.println("             SALES INVOICE & RECEIPT                     ");
        System.out.println("=========================================================");
        System.out.println(" Invoice No   : " + sale.getSaleId());
        System.out.println(" Date & Time  : " + dateStr);
        System.out.println(" Customer ID  : " + sale.getCustomerId());
        System.out.println(" Payment Mode : " + sale.getPaymentMethod());
        System.out.println("---------------------------------------------------------");
        System.out.printf("%-25s | %-6s | %-10s | %-10s%n", "Item Name", "Qty", "Unit Price", "Subtotal");
        System.out.println("---------------------------------------------------------");

        if (sale.getItems() != null) {
            for (SaleItem item : sale.getItems()) {
                System.out.printf("%-25s | %-6d | Rs. %-6.2f | Rs. %-6.2f%n",
                        truncate(item.getProductName(), 25),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal());
            }
        }

        System.out.println("---------------------------------------------------------");
        System.out.printf(" TOTAL AMOUNT DUE / PAID               : Rs. %.2f%n", sale.getTotalAmount());
        System.out.println("=========================================================");
        System.out.println("       Thank you for your business! Visit again!         ");
        System.out.println("=========================================================");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
