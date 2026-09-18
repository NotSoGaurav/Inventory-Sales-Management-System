package com.inventory.repository;

import com.inventory.enums.PaymentMethod;
import com.inventory.model.Sale;
import com.inventory.model.SaleItem;
import com.inventory.util.FileUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * File-based repository for persisting and retrieving Sale transactions.
 * Handles nested collections of SaleItems with composite serialization.
 */
public class SaleRepository {

    private final String filePath;

    public SaleRepository() {
        this("data/sales.txt");
    }

    public SaleRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves a new sale record to file.
     *
     * @param sale the sale to save
     * @return true if saved, false if sale is null or ID already exists
     */
    public boolean save(Sale sale) {
        if (sale == null || sale.getSaleId() == null || sale.getSaleId().trim().isEmpty()) {
            return false;
        }

        if (existsById(sale.getSaleId())) {
            System.err.println("[SaleRepository Warning] Sale with ID '" + sale.getSaleId() + "' already exists.");
            return false;
        }

        // Recalculate total amount prior to persistence to guarantee data integrity
        sale.calculateTotalAmount();
        String serialized = serialize(sale);
        return FileUtil.appendLine(filePath, serialized);
    }

    /**
     * Finds a sale by its sale ID.
     *
     * @param saleId the sale ID
     * @return an Optional containing the found sale, or empty Optional
     */
    public Optional<Sale> findById(String saleId) {
        if (saleId == null || saleId.trim().isEmpty()) {
            return Optional.empty();
        }

        return findAll().stream()
                .filter(s -> s.getSaleId().equalsIgnoreCase(saleId.trim()))
                .findFirst();
    }

    /**
     * Retrieves all sales from the file.
     *
     * @return list of all valid sales
     */
    public List<Sale> findAll() {
        List<Sale> sales = new ArrayList<>();
        List<String> lines = FileUtil.readAllLines(filePath);

        for (String line : lines) {
            Sale sale = deserialize(line);
            if (sale != null) {
                sales.add(sale);
            }
        }

        return sales;
    }

    /**
     * Finds all sales associated with a specific customer ID.
     *
     * @param customerId the customer ID
     * @return list of matching sales
     */
    public List<Sale> findByCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return findAll().stream()
                .filter(s -> s.getCustomerId() != null && s.getCustomerId().equalsIgnoreCase(customerId.trim()))
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing sale record in the file.
     *
     * @param updatedSale the updated sale record
     * @return true if updated, false if sale not found or invalid
     */
    public boolean update(Sale updatedSale) {
        if (updatedSale == null || updatedSale.getSaleId() == null) {
            return false;
        }

        updatedSale.calculateTotalAmount();
        List<Sale> sales = findAll();
        boolean found = false;

        for (int i = 0; i < sales.size(); i++) {
            if (sales.get(i).getSaleId().equalsIgnoreCase(updatedSale.getSaleId().trim())) {
                sales.set(i, updatedSale);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        List<String> serializedLines = new ArrayList<>();
        for (Sale s : sales) {
            serializedLines.add(serialize(s));
        }

        return FileUtil.writeAllLines(filePath, serializedLines);
    }

    /**
     * Deletes a sale by its sale ID.
     *
     * @param saleId the ID of the sale to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteById(String saleId) {
        if (saleId == null || saleId.trim().isEmpty()) {
            return false;
        }

        List<Sale> sales = findAll();
        boolean removed = sales.removeIf(s -> s.getSaleId().equalsIgnoreCase(saleId.trim()));

        if (!removed) {
            return false;
        }

        List<String> serializedLines = new ArrayList<>();
        for (Sale s : sales) {
            serializedLines.add(serialize(s));
        }

        return FileUtil.writeAllLines(filePath, serializedLines);
    }

    /**
     * Checks if a sale exists with the given ID.
     *
     * @param saleId the sale ID
     * @return true if found, false otherwise
     */
    public boolean existsById(String saleId) {
        return findById(saleId).isPresent();
    }

    /**
     * Serializes a Sale object into a pipe-separated string line with nested items.
     * Format: saleId|customerId|saleDate|paymentMethod|totalAmount|items
     * Items format: productId~productName~quantity~unitPrice (separated by ;)
     */
    private String serialize(Sale sale) {
        StringBuilder itemsStr = new StringBuilder();
        if (sale.getItems() != null) {
            for (int i = 0; i < sale.getItems().size(); i++) {
                SaleItem item = sale.getItems().get(i);
                if (item != null) {
                    if (i > 0) {
                        itemsStr.append(FileUtil.ITEM_SEPARATOR);
                    }
                    itemsStr.append(item.getProductId() != null ? item.getProductId() : "").append(FileUtil.SUB_ITEM_SEPARATOR)
                            .append(item.getProductName() != null ? item.getProductName() : "").append(FileUtil.SUB_ITEM_SEPARATOR)
                            .append(item.getQuantity()).append(FileUtil.SUB_ITEM_SEPARATOR)
                            .append(item.getUnitPrice());
                }
            }
        }

        String saleDateStr = sale.getSaleDate() != null ? sale.getSaleDate().toString() : LocalDateTime.now().toString();
        String paymentMethodStr = sale.getPaymentMethod() != null ? sale.getPaymentMethod().name() : PaymentMethod.CASH.name();

        return sale.getSaleId() + FileUtil.FIELD_SEPARATOR
                + (sale.getCustomerId() != null ? sale.getCustomerId() : "") + FileUtil.FIELD_SEPARATOR
                + saleDateStr + FileUtil.FIELD_SEPARATOR
                + paymentMethodStr + FileUtil.FIELD_SEPARATOR
                + sale.getTotalAmount() + FileUtil.FIELD_SEPARATOR
                + itemsStr.toString();
    }

    /**
     * Deserializes a composite pipe-separated line into a Sale object.
     * Handles nested SaleItems and malformed lines safely.
     */
    private Sale deserialize(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = line.split(FileUtil.FIELD_DELIMITER_REGEX, -1);
            if (parts.length < 5) {
                System.err.println("[SaleRepository Warning] Malformed sale line skipped (insufficient fields): " + line);
                return null;
            }

            String saleId = parts[0].trim();
            String customerId = parts[1].trim();

            LocalDateTime saleDate;
            try {
                saleDate = LocalDateTime.parse(parts[2].trim());
            } catch (DateTimeParseException e) {
                try {
                    saleDate = LocalDate.parse(parts[2].trim()).atStartOfDay();
                } catch (Exception ex) {
                    saleDate = LocalDateTime.now();
                }
            }

            PaymentMethod paymentMethod;
            try {
                paymentMethod = PaymentMethod.valueOf(parts[3].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                paymentMethod = PaymentMethod.CASH;
            }

            double totalAmount = 0.0;
            try {
                totalAmount = Double.parseDouble(parts[4].trim());
            } catch (NumberFormatException e) {
                System.err.println("[SaleRepository Warning] Invalid total amount in sale record: " + line);
            }

            List<SaleItem> items = new ArrayList<>();
            if (parts.length >= 6 && !parts[5].trim().isEmpty()) {
                String[] itemTokens = parts[5].trim().split(FileUtil.ITEM_DELIMITER_REGEX);
                for (String token : itemTokens) {
                    if (token.trim().isEmpty()) {
                        continue;
                    }
                    String[] subParts = token.split(FileUtil.SUB_ITEM_DELIMITER_REGEX, -1);
                    if (subParts.length >= 4) {
                        try {
                            String productId = subParts[0].trim();
                            String productName = subParts[1].trim();
                            int quantity = Integer.parseInt(subParts[2].trim());
                            double unitPrice = Double.parseDouble(subParts[3].trim());

                            items.add(new SaleItem(productId, productName, quantity, unitPrice));
                        } catch (NumberFormatException e) {
                            System.err.println("[SaleRepository Warning] Invalid number in sale item: " + token);
                        }
                    }
                }
            }

            Sale sale = new Sale(saleId, customerId, saleDate, items, paymentMethod);
            // Ensure stored total matches
            if (totalAmount > 0) {
                sale.setTotalAmount(totalAmount);
            }
            return sale;
        } catch (Exception e) {
            System.err.println("[SaleRepository Warning] Failed to parse sale record: " + line + " - " + e.getMessage());
            return null;
        }
    }
}
