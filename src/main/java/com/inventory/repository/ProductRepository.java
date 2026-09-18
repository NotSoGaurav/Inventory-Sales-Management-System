package com.inventory.repository;

import com.inventory.model.Product;
import com.inventory.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * File-based repository for persisting and retrieving Product entities.
 */
public class ProductRepository {

    private final String filePath;

    public ProductRepository() {
        this("data/products.txt");
    }

    public ProductRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves a new product record to file.
     *
     * @param product the product to save
     * @return true if saved successfully, false if product is null or ID already exists
     */
    public boolean save(Product product) {
        if (product == null || product.getProductId() == null || product.getProductId().trim().isEmpty()) {
            return false;
        }

        if (existsById(product.getProductId())) {
            System.err.println("[ProductRepository Warning] Product with ID '" + product.getProductId() + "' already exists.");
            return false;
        }

        String serialized = serialize(product);
        return FileUtil.appendLine(filePath, serialized);
    }

    /**
     * Finds a product by its product ID.
     *
     * @param productId the product ID
     * @return an Optional containing the product if found, or empty Optional otherwise
     */
    public Optional<Product> findById(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return Optional.empty();
        }

        return findAll().stream()
                .filter(p -> p.getProductId().equalsIgnoreCase(productId.trim()))
                .findFirst();
    }

    /**
     * Retrieves all products from the file.
     *
     * @return list of all valid products
     */
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        List<String> lines = FileUtil.readAllLines(filePath);

        for (String line : lines) {
            Product product = deserialize(line);
            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }

    /**
     * Finds products belonging to a given category.
     *
     * @param category the category to search for
     * @return list of matching products
     */
    public List<Product> findByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return findAll().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(category.trim()))
                .collect(Collectors.toList());
    }

    /**
     * Finds products associated with a specific supplier ID.
     *
     * @param supplierId the supplier ID
     * @return list of matching products
     */
    public List<Product> findBySupplierId(String supplierId) {
        if (supplierId == null || supplierId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return findAll().stream()
                .filter(p -> p.getSupplierId() != null && p.getSupplierId().equalsIgnoreCase(supplierId.trim()))
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing product record in the file.
     *
     * @param updatedProduct the product with updated details
     * @return true if updated successfully, false if product not found or invalid
     */
    public boolean update(Product updatedProduct) {
        if (updatedProduct == null || updatedProduct.getProductId() == null) {
            return false;
        }

        List<Product> products = findAll();
        boolean found = false;

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId().equalsIgnoreCase(updatedProduct.getProductId().trim())) {
                products.set(i, updatedProduct);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        List<String> serializedLines = new ArrayList<>();
        for (Product p : products) {
            serializedLines.add(serialize(p));
        }

        return FileUtil.writeAllLines(filePath, serializedLines);
    }

    /**
     * Deletes a product by its product ID.
     *
     * @param productId the ID of the product to delete
     * @return true if deleted, false if not found or invalid ID
     */
    public boolean deleteById(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }

        List<Product> products = findAll();
        boolean removed = products.removeIf(p -> p.getProductId().equalsIgnoreCase(productId.trim()));

        if (!removed) {
            return false;
        }

        List<String> serializedLines = new ArrayList<>();
        for (Product p : products) {
            serializedLines.add(serialize(p));
        }

        return FileUtil.writeAllLines(filePath, serializedLines);
    }

    /**
     * Checks if a product exists with the given ID.
     *
     * @param productId the product ID
     * @return true if found, false otherwise
     */
    public boolean existsById(String productId) {
        return findById(productId).isPresent();
    }

    /**
     * Serializes a Product object into a pipe-separated string line.
     * Format: productId|name|category|price|quantity|minStockThreshold|supplierId
     */
    private String serialize(Product product) {
        return product.getProductId() + FileUtil.FIELD_SEPARATOR
                + product.getName() + FileUtil.FIELD_SEPARATOR
                + product.getCategory() + FileUtil.FIELD_SEPARATOR
                + product.getPrice() + FileUtil.FIELD_SEPARATOR
                + product.getQuantity() + FileUtil.FIELD_SEPARATOR
                + product.getMinStockThreshold() + FileUtil.FIELD_SEPARATOR
                + (product.getSupplierId() != null ? product.getSupplierId() : "");
    }

    /**
     * Deserializes a pipe-separated string line into a Product object.
     * Handles malformed records gracefully.
     */
    private Product deserialize(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = line.split(FileUtil.FIELD_DELIMITER_REGEX, -1);
            if (parts.length < 7) {
                System.err.println("[ProductRepository Warning] Malformed product line skipped (insufficient fields): " + line);
                return null;
            }

            String productId = parts[0].trim();
            String name = parts[1].trim();
            String category = parts[2].trim();
            double price = Double.parseDouble(parts[3].trim());
            int quantity = Integer.parseInt(parts[4].trim());
            int minStockThreshold = Integer.parseInt(parts[5].trim());
            String supplierId = parts[6].trim();

            return new Product(productId, name, category, price, quantity, minStockThreshold, supplierId);
        } catch (NumberFormatException e) {
            System.err.println("[ProductRepository Warning] Numeric parse error in product record: " + line + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("[ProductRepository Warning] Failed to parse product record: " + line + " - " + e.getMessage());
            return null;
        }
    }
}
