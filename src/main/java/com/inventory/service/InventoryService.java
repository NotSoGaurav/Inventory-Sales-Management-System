package com.inventory.service;

import com.inventory.enums.StockStatus;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import com.inventory.util.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer responsible for inventory operations, stock adjustments,
 * threshold monitoring, and stock status tracking.
 */
public class InventoryService {

    private final ProductRepository productRepository;

    public InventoryService() {
        this(new ProductRepository());
    }

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves the entire product inventory with current stock levels.
     *
     * @return list of all inventory products
     */
    public List<Product> getAllInventory() {
        return productRepository.findAll();
    }

    /**
     * Retrieves the stock information for a specific product ID.
     *
     * @param productId the product ID
     * @return the product with its current stock
     * @throws ProductNotFoundException if product does not exist
     * @throws ValidationException      if product ID is blank
     */
    public Product getStock(String productId) throws ProductNotFoundException, ValidationException {
        InputValidator.validateId(productId, "Product ID");

        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID '" + productId + "' not found."));
    }

    /**
     * Increases stock for a product (e.g., restock / inventory received).
     *
     * @param productId the product ID
     * @param amount    the quantity to add (must be > 0)
     * @return the updated product
     * @throws ProductNotFoundException if product does not exist
     * @throws ValidationException      if amount <= 0 or ID is invalid
     */
    public Product increaseStock(String productId, int amount) throws ProductNotFoundException, ValidationException {
        InputValidator.validateId(productId, "Product ID");

        if (amount <= 0) {
            throw new ValidationException("Stock increase amount must be greater than 0.");
        }

        Product product = getStock(productId);
        int updatedQty = product.getQuantity() + amount;
        product.setQuantity(updatedQty);

        boolean updated = productRepository.update(product);
        if (!updated) {
            throw new ValidationException("Failed to update inventory for product ID: " + productId);
        }

        return product;
    }

    /**
     * Decreases stock for a product (e.g., damage, write-off, manual adjustment).
     * Prevents negative stock and detects insufficient stock conditions.
     *
     * @param productId the product ID
     * @param amount    the quantity to deduct (must be > 0)
     * @return the updated product
     * @throws ProductNotFoundException   if product does not exist
     * @throws InsufficientStockException if available quantity is less than requested amount
     * @throws ValidationException        if amount <= 0 or ID is invalid
     */
    public Product decreaseStock(String productId, int amount) throws ProductNotFoundException, InsufficientStockException, ValidationException {
        InputValidator.validateId(productId, "Product ID");

        if (amount <= 0) {
            throw new ValidationException("Stock decrease amount must be greater than 0.");
        }

        Product product = getStock(productId);

        if (product.getQuantity() < amount) {
            throw new InsufficientStockException(productId, amount, product.getQuantity());
        }

        int updatedQty = product.getQuantity() - amount;
        product.setQuantity(updatedQty);

        boolean updated = productRepository.update(product);
        if (!updated) {
            throw new ValidationException("Failed to update inventory for product ID: " + productId);
        }

        return product;
    }

    /**
     * Sets an absolute stock quantity (e.g., following a physical stock audit).
     *
     * @param productId   the product ID
     * @param newQuantity the new quantity (must be >= 0)
     * @return the updated product
     * @throws ProductNotFoundException if product does not exist
     * @throws ValidationException      if newQuantity < 0 or ID is invalid
     */
    public Product updateStock(String productId, int newQuantity) throws ProductNotFoundException, ValidationException {
        InputValidator.validateId(productId, "Product ID");

        if (newQuantity < 0) {
            throw new ValidationException("Stock quantity cannot be negative.");
        }

        Product product = getStock(productId);
        product.setQuantity(newQuantity);

        boolean updated = productRepository.update(product);
        if (!updated) {
            throw new ValidationException("Failed to update stock quantity for product ID: " + productId);
        }

        return product;
    }

    /**
     * Retrieves all products identified as having low stock.
     *
     * @return list of low-stock products
     */
    public List<Product> getLowStockProducts() {
        return getProductsByStockStatus(StockStatus.LOW_STOCK);
    }

    /**
     * Retrieves all products that are completely out of stock.
     *
     * @return list of out-of-stock products
     */
    public List<Product> getOutOfStockProducts() {
        return getProductsByStockStatus(StockStatus.OUT_OF_STOCK);
    }

    /**
     * Retrieves products filtered by their StockStatus enum.
     *
     * @param status the StockStatus to match
     * @return list of matching products
     */
    public List<Product> getProductsByStockStatus(StockStatus status) {
        if (status == null) {
            return new ArrayList<>();
        }

        return productRepository.findAll().stream()
                .filter(p -> p.getStockStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Checks whether sufficient stock is available for a requested quantity.
     *
     * @param productId        the product ID
     * @param requiredQuantity the required quantity
     * @return true if product exists and quantity >= requiredQuantity, false otherwise
     */
    public boolean isStockAvailable(String productId, int requiredQuantity) {
        if (productId == null || requiredQuantity <= 0) {
            return false;
        }

        return productRepository.findById(productId)
                .map(p -> p.getQuantity() >= requiredQuantity)
                .orElse(false);
    }
}
