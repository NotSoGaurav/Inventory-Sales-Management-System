package com.inventory.service;

import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.model.Product;
import com.inventory.model.Sale;
import com.inventory.model.SaleItem;
import com.inventory.repository.CustomerRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SaleRepository;
import com.inventory.util.InputValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class handling sales transactions, item validation, stock verification,
 * atomic stock reduction, and bill generation logic.
 */
public class SalesService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InventoryService inventoryService;

    public SalesService() {
        this(new SaleRepository(), new ProductRepository(), new CustomerRepository(), new InventoryService());
    }

    public SalesService(SaleRepository saleRepository, ProductRepository productRepository,
                        CustomerRepository customerRepository, InventoryService inventoryService) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.inventoryService = inventoryService;
    }

    /**
     * Generates a unique sequential sale ID.
     *
     * @return generated sale ID string (e.g. SALE-1001)
     */
    public String generateSaleId() {
        int count = saleRepository.findAll().size();
        return "SALE-" + (1001 + count);
    }

    /**
     * Creates and validates a single SaleItem for a product and quantity.
     *
     * @param productId product ID
     * @param quantity  requested quantity
     * @return the constructed SaleItem
     * @throws ProductNotFoundException   if product does not exist
     * @throws InsufficientStockException if requested quantity exceeds available stock
     * @throws ValidationException        if quantity <= 0 or ID is blank
     */
    public SaleItem createSaleItem(String productId, int quantity)
            throws ProductNotFoundException, InsufficientStockException, ValidationException {
        InputValidator.validateId(productId, "Product ID");

        if (quantity <= 0) {
            throw new ValidationException("Sale quantity must be greater than 0.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID '" + productId + "' not found."));

        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getQuantity());
        }

        return new SaleItem(product.getProductId(), product.getName(), quantity, product.getPrice());
    }

    /**
     * Processes a complete sale transaction:
     * 1. Validates customer and payment method.
     * 2. Pre-validates inventory stock across ALL items before modifying any state.
     * 3. Atomically reduces inventory for all line items.
     * 4. Persists the completed sale to disk.
     *
     * @param sale the sale to process
     * @return the finalized, persisted Sale object
     * @throws ProductNotFoundException   if an item product is missing
     * @throws InsufficientStockException if stock is insufficient for any item
     * @throws ValidationException        if validation fails
     */
    public Sale processSale(Sale sale)
            throws ProductNotFoundException, InsufficientStockException, ValidationException {
        if (sale == null) {
            throw new ValidationException("Sale cannot be null.");
        }

        InputValidator.validateId(sale.getSaleId(), "Sale ID");

        if (saleRepository.existsById(sale.getSaleId())) {
            throw new ValidationException("Sale with ID '" + sale.getSaleId() + "' already exists.");
        }

        if (sale.getCustomerId() == null || sale.getCustomerId().trim().isEmpty()) {
            throw new ValidationException("Customer ID is required for checkout.");
        }

        String customerId = sale.getCustomerId().trim();
        if (!customerRepository.existsById(customerId)) {
            throw new ValidationException("Customer with ID '" + customerId + "' is not registered.");
        }

        if (sale.getItems() == null || sale.getItems().isEmpty()) {
            throw new ValidationException("Sale must contain at least one item.");
        }

        if (sale.getPaymentMethod() == null) {
            throw new ValidationException("Payment method is required.");
        }

        // STEP 1: Pre-validate inventory for ALL items prior to updating any stock
        for (SaleItem item : sale.getItems()) {
            Product prod = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product ID '" + item.getProductId() + "' not found."));

            if (prod.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(item.getProductId(), item.getQuantity(), prod.getQuantity());
            }
        }

        // STEP 2: Reduce stock for all validated items
        for (SaleItem item : sale.getItems()) {
            inventoryService.decreaseStock(item.getProductId(), item.getQuantity());
        }

        // STEP 3: Finalize date and calculate total
        if (sale.getSaleDate() == null) {
            sale.setSaleDate(LocalDateTime.now());
        }
        sale.calculateTotalAmount();

        // STEP 4: Persist the sale record
        boolean saved = saleRepository.save(sale);
        if (!saved) {
            throw new ValidationException("Failed to persist sale to storage.");
        }

        com.inventory.util.LoggerUtil.logOperation("SALE_COMPLETED", sale.getSaleId(),
                "Customer: " + sale.getCustomerId() + " | Total: Rs. " + sale.getTotalAmount());

        return sale;
    }

    /**
     * Retrieves all recorded sales.
     *
     * @return list of sales
     */
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    /**
     * Finds a sale by ID.
     *
     * @param saleId the sale ID
     * @return an Optional containing the sale
     * @throws ValidationException if sale ID is blank
     */
    public Optional<Sale> getSaleById(String saleId) throws ValidationException {
        InputValidator.validateId(saleId, "Sale ID");
        return saleRepository.findById(saleId);
    }

    /**
     * Retrieves all sales for a given customer ID.
     *
     * @param customerId the customer ID
     * @return list of sales for the customer
     */
    public List<Sale> getSalesByCustomer(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return saleRepository.findByCustomerId(customerId.trim());
    }

    /**
     * Calculates the cumulative revenue generated from all recorded sales.
     *
     * @return total revenue amount
     */
    public double getTotalRevenue() {
        double total = 0.0;
        for (Sale s : saleRepository.findAll()) {
            total += s.getTotalAmount();
        }
        return total;
    }
}
