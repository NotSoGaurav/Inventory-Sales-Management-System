package com.inventory.ui;

import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.model.Product;
import com.inventory.service.ProductService;
import com.inventory.util.InputValidator;

import java.util.List;
import java.util.Scanner;

/**
 * Console UI layer for Product Management.
 * Handles menu presentation, reading user inputs, and displaying formatted results.
 */
public class ProductUI {

    private final ProductService productService;
    private final Scanner scanner;

    public ProductUI() {
        this(new ProductService(), new Scanner(System.in));
    }

    public ProductUI(ProductService productService, Scanner scanner) {
        this.productService = productService;
        this.scanner = scanner;
    }

    /**
     * Displays the Product Management menu loop.
     */
    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n==================================================");
            System.out.println("               PRODUCT MANAGEMENT                 ");
            System.out.println("==================================================");
            System.out.println(" 1. Add New Product");
            System.out.println(" 2. View All Products");
            System.out.println(" 3. Search Products");
            System.out.println(" 4. Find Product by ID");
            System.out.println(" 5. Update Product");
            System.out.println(" 6. Delete Product");
            System.out.println(" 7. View Products by Category");
            System.out.println(" 8. Back to Main Menu");
            System.out.println("==================================================");
            System.out.print("Enter your choice (1-8): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleAddProduct();
                    break;
                case "2":
                    handleViewAllProducts();
                    break;
                case "3":
                    handleSearchProducts();
                    break;
                case "4":
                    handleFindById();
                    break;
                case "5":
                    handleUpdateProduct();
                    break;
                case "6":
                    handleDeleteProduct();
                    break;
                case "7":
                    handleViewByCategory();
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

    private void handleAddProduct() {
        System.out.println("\n--- Add New Product ---");
        try {
            System.out.print("Enter Product ID: ");
            String id = scanner.nextLine().trim();

            System.out.print("Enter Product Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Category: ");
            String category = scanner.nextLine().trim();

            System.out.print("Enter Unit Price (e.g. 1200.0): ");
            String priceInput = scanner.nextLine().trim();
            double price = InputValidator.parsePositiveDouble(priceInput, "Price");

            System.out.print("Enter Initial Quantity: ");
            String qtyInput = scanner.nextLine().trim();
            int quantity = InputValidator.parseNonNegativeInt(qtyInput, "Quantity");

            System.out.print("Enter Minimum Stock Threshold (e.g. 5): ");
            String thresholdInput = scanner.nextLine().trim();
            int threshold = InputValidator.parseNonNegativeInt(thresholdInput, "Minimum stock threshold");

            System.out.print("Enter Supplier ID (optional, press Enter to skip): ");
            String supplierId = scanner.nextLine().trim();

            Product product = new Product(id, name, category, price, quantity, threshold, supplierId);
            productService.addProduct(product);

            System.out.println("[Success] Product '" + name + "' added successfully with ID: " + id);
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Error] Failed to add product: " + e.getMessage());
        }
    }

    private void handleViewAllProducts() {
        System.out.println("\n--- All Products ---");
        List<Product> products = productService.getAllProducts();
        displayProductTable(products);
    }

    private void handleSearchProducts() {
        System.out.println("\n--- Search Products ---");
        System.out.print("Enter search keyword (name, category, or ID): ");
        String query = scanner.nextLine().trim();

        List<Product> results = productService.searchProducts(query);
        displayProductTable(results);
    }

    private void handleFindById() {
        System.out.println("\n--- Find Product by ID ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();

        try {
            Product product = productService.getProductById(id);
            displayProductDetails(product);
        } catch (ProductNotFoundException e) {
            System.out.println("[Not Found] " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void handleUpdateProduct() {
        System.out.println("\n--- Update Product ---");
        System.out.print("Enter Product ID to update: ");
        String id = scanner.nextLine().trim();

        try {
            Product existing = productService.getProductById(id);
            System.out.println("Product found: " + existing.getName());
            System.out.println("(Leave empty and press Enter to keep current value)");

            System.out.print("New Name [" + existing.getName() + "]: ");
            String nameInput = scanner.nextLine().trim();
            String name = nameInput.isEmpty() ? existing.getName() : nameInput;

            System.out.print("New Category [" + existing.getCategory() + "]: ");
            String categoryInput = scanner.nextLine().trim();
            String category = categoryInput.isEmpty() ? existing.getCategory() : categoryInput;

            System.out.print("New Price [" + existing.getPrice() + "]: ");
            String priceInput = scanner.nextLine().trim();
            double price = priceInput.isEmpty() ? existing.getPrice() : InputValidator.parsePositiveDouble(priceInput, "Price");

            System.out.print("New Quantity [" + existing.getQuantity() + "]: ");
            String qtyInput = scanner.nextLine().trim();
            int quantity = qtyInput.isEmpty() ? existing.getQuantity() : InputValidator.parseNonNegativeInt(qtyInput, "Quantity");

            System.out.print("New Min Stock Threshold [" + existing.getMinStockThreshold() + "]: ");
            String thresholdInput = scanner.nextLine().trim();
            int threshold = thresholdInput.isEmpty() ? existing.getMinStockThreshold() : InputValidator.parseNonNegativeInt(thresholdInput, "Min threshold");

            System.out.print("New Supplier ID [" + existing.getSupplierId() + "]: ");
            String supplierInput = scanner.nextLine().trim();
            String supplierId = supplierInput.isEmpty() ? existing.getSupplierId() : supplierInput;

            Product updated = new Product(existing.getProductId(), name, category, price, quantity, threshold, supplierId);
            productService.updateProduct(updated);

            System.out.println("[Success] Product updated successfully!");
        } catch (ProductNotFoundException e) {
            System.out.println("[Not Found] " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void handleDeleteProduct() {
        System.out.println("\n--- Delete Product ---");
        System.out.print("Enter Product ID to delete: ");
        String id = scanner.nextLine().trim();

        try {
            Product product = productService.getProductById(id);
            System.out.print("Are you sure you want to delete '" + product.getName() + "' (" + id + ")? (y/n): ");
            String confirm = scanner.nextLine().trim();

            if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
                productService.deleteProduct(id);
                System.out.println("[Success] Product deleted successfully.");
            } else {
                System.out.println("[Cancelled] Deletion cancelled.");
            }
        } catch (ProductNotFoundException e) {
            System.out.println("[Not Found] " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void handleViewByCategory() {
        System.out.println("\n--- View Products by Category ---");
        System.out.print("Enter Category name: ");
        String category = scanner.nextLine().trim();

        List<Product> products = productService.getProductsByCategory(category);
        displayProductTable(products);
    }

    private void displayProductTable(List<Product> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-25s | %-15s | %-10s | %-8s | %-10s | %-12s | %-12s%n",
                "ID", "Name", "Category", "Price", "Qty", "Min Stock", "Supplier ID", "Status");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (Product p : products) {
            System.out.printf("%-10s | %-25s | %-15s | %-10.2f | %-8d | %-10d | %-12s | %-12s%n",
                    p.getProductId(),
                    truncate(p.getName(), 25),
                    truncate(p.getCategory(), 15),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getMinStockThreshold(),
                    p.getSupplierId() != null ? p.getSupplierId() : "-",
                    p.getStockStatus());
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println("Total products listed: " + products.size());
    }

    private void displayProductDetails(Product p) {
        System.out.println("\n==========================================");
        System.out.println("             PRODUCT DETAILS              ");
        System.out.println("==========================================");
        System.out.println(" Product ID    : " + p.getProductId());
        System.out.println(" Name          : " + p.getName());
        System.out.println(" Category      : " + p.getCategory());
        System.out.printf(" Price         : %.2f%n", p.getPrice());
        System.out.println(" Quantity      : " + p.getQuantity());
        System.out.println(" Min Threshold : " + p.getMinStockThreshold());
        System.out.println(" Supplier ID   : " + (p.getSupplierId() != null && !p.getSupplierId().isEmpty() ? p.getSupplierId() : "None"));
        System.out.println(" Stock Status  : " + p.getStockStatus());
        System.out.println("==========================================");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
