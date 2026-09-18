package com.inventory.service;

import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SupplierRepository;
import com.inventory.util.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class encapsulating business rules and operations for Product management.
 */
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductService() {
        this(new ProductRepository(), new SupplierRepository());
    }

    public ProductService(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    /**
     * Adds a new product after performing domain validation.
     *
     * @param product the product to add
     * @return the saved product
     * @throws ValidationException if validation rules fail or ID already exists
     */
    public Product addProduct(Product product) throws ValidationException {
        if (product == null) {
            throw new ValidationException("Product cannot be null.");
        }

        // Validate individual fields
        InputValidator.validateId(product.getProductId(), "Product ID");
        InputValidator.validateRequiredText(product.getName(), "Product name");
        InputValidator.validateRequiredText(product.getCategory(), "Product category");

        if (product.getPrice() <= 0.0) {
            throw new ValidationException("Price must be greater than 0.");
        }

        if (product.getQuantity() < 0) {
            throw new ValidationException("Quantity cannot be negative.");
        }

        if (product.getMinStockThreshold() < 0) {
            throw new ValidationException("Minimum stock threshold cannot be negative.");
        }

        // Check for duplicate ID
        if (productRepository.existsById(product.getProductId())) {
            throw new ValidationException("Product with ID '" + product.getProductId() + "' already exists.");
        }

        // Validate supplier existence if supplier ID is provided
        if (product.getSupplierId() != null && !product.getSupplierId().trim().isEmpty()) {
            InputValidator.validateNoDelimiters(product.getSupplierId(), "Supplier ID");
            String trimmedSupplierId = product.getSupplierId().trim();
            if (!supplierRepository.existsById(trimmedSupplierId)) {
                // If supplier doesn't exist, we log a warning or enforce association
                System.out.println("[Notice] Supplier ID '" + trimmedSupplierId + "' is not currently registered in suppliers.txt.");
            }
        }

        boolean saved = productRepository.save(product);
        if (!saved) {
            throw new ValidationException("Failed to persist product to storage.");
        }

        return product;
    }

    /**
     * Retrieves all products.
     *
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Finds a product by its ID.
     *
     * @param productId the ID to search
     * @return the found Product
     * @throws ProductNotFoundException if product does not exist
     * @throws ValidationException      if product ID is blank
     */
    public Product getProductById(String productId) throws ProductNotFoundException, ValidationException {
        InputValidator.validateId(productId, "Product ID");

        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID '" + productId + "' not found."));
    }

    /**
     * Searches products by matching name, category, or ID (case-insensitive substring match).
     *
     * @param query the search query
     * @return list of matching products
     */
    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }

        String lowerQuery = query.trim().toLowerCase();
        return productRepository.findAll().stream()
                .filter(p -> (p.getProductId() != null && p.getProductId().toLowerCase().contains(lowerQuery)) ||
                        (p.getName() != null && p.getName().toLowerCase().contains(lowerQuery)) ||
                        (p.getCategory() != null && p.getCategory().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing product.
     *
     * @param product the updated product
     * @return true if updated successfully
     * @throws ProductNotFoundException if product does not exist
     * @throws ValidationException      if validation rules fail
     */
    public boolean updateProduct(Product product) throws ProductNotFoundException, ValidationException {
        if (product == null) {
            throw new ValidationException("Product cannot be null.");
        }

        InputValidator.validateId(product.getProductId(), "Product ID");
        InputValidator.validateRequiredText(product.getName(), "Product name");
        InputValidator.validateRequiredText(product.getCategory(), "Product category");

        if (product.getPrice() <= 0.0) {
            throw new ValidationException("Price must be greater than 0.");
        }

        if (product.getQuantity() < 0) {
            throw new ValidationException("Quantity cannot be negative.");
        }

        if (product.getMinStockThreshold() < 0) {
            throw new ValidationException("Minimum stock threshold cannot be negative.");
        }

        if (!productRepository.existsById(product.getProductId())) {
            throw new ProductNotFoundException("Cannot update. Product with ID '" + product.getProductId() + "' does not exist.");
        }

        return productRepository.update(product);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId the ID of the product to delete
     * @return true if deleted successfully
     * @throws ProductNotFoundException if product does not exist
     * @throws ValidationException      if ID is blank
     */
    public boolean deleteProduct(String productId) throws ProductNotFoundException, ValidationException {
        InputValidator.validateId(productId, "Product ID");

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Cannot delete. Product with ID '" + productId + "' does not exist.");
        }

        return productRepository.deleteById(productId);
    }

    /**
     * Retrieves products by category name.
     *
     * @param category the category
     * @return list of matching products
     */
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return productRepository.findByCategory(category.trim());
    }

    /**
     * Retrieves products associated with a supplier.
     *
     * @param supplierId the supplier ID
     * @return list of matching products
     */
    public List<Product> getProductsBySupplier(String supplierId) {
        if (supplierId == null || supplierId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return productRepository.findBySupplierId(supplierId.trim());
    }

    /**
     * Checks if a product exists.
     *
     * @param productId the product ID
     * @return true if product exists, false otherwise
     */
    public boolean existsById(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }
        return productRepository.existsById(productId.trim());
    }
}
