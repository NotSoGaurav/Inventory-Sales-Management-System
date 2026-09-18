package com.inventory.repository;

import com.inventory.model.Supplier;
import com.inventory.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File-based repository for persisting and retrieving Supplier entities.
 */
public class SupplierRepository {

    private final String filePath;

    public SupplierRepository() {
        this("data/suppliers.txt");
    }

    public SupplierRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves a new supplier record to file.
     *
     * @param supplier the supplier to save
     * @return true if saved, false if supplier is null or ID already exists
     */
    public boolean save(Supplier supplier) {
        if (supplier == null || supplier.getSupplierId() == null || supplier.getSupplierId().trim().isEmpty()) {
            return false;
        }

        if (existsById(supplier.getSupplierId())) {
            System.err.println("[SupplierRepository Warning] Supplier with ID '" + supplier.getSupplierId() + "' already exists.");
            return false;
        }

        String serialized = serialize(supplier);
        return FileUtil.appendLine(filePath, serialized);
    }

    /**
     * Finds a supplier by supplier ID.
     *
     * @param supplierId the supplier ID
     * @return an Optional containing the found supplier, or empty if not found
     */
    public Optional<Supplier> findById(String supplierId) {
        if (supplierId == null || supplierId.trim().isEmpty()) {
            return Optional.empty();
        }

        return findAll().stream()
                .filter(s -> s.getSupplierId().equalsIgnoreCase(supplierId.trim()))
                .findFirst();
    }

    /**
     * Retrieves all suppliers from the file.
     *
     * @return list of all valid suppliers
     */
    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        List<String> lines = FileUtil.readAllLines(filePath);

        for (String line : lines) {
            Supplier supplier = deserialize(line);
            if (supplier != null) {
                suppliers.add(supplier);
            }
        }

        return suppliers;
    }

    /**
     * Updates an existing supplier record.
     *
     * @param updatedSupplier the supplier with updated data
     * @return true if updated, false if supplier is null or not found
     */
    public boolean update(Supplier updatedSupplier) {
        if (updatedSupplier == null || updatedSupplier.getSupplierId() == null) {
            return false;
        }

        List<Supplier> suppliers = findAll();
        boolean found = false;

        for (int i = 0; i < suppliers.size(); i++) {
            if (suppliers.get(i).getSupplierId().equalsIgnoreCase(updatedSupplier.getSupplierId().trim())) {
                suppliers.set(i, updatedSupplier);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        List<String> serializedLines = new ArrayList<>();
        for (Supplier s : suppliers) {
            serializedLines.add(serialize(s));
        }

        return FileUtil.writeAllLines(filePath, serializedLines);
    }

    /**
     * Deletes a supplier by supplier ID.
     *
     * @param supplierId the ID of the supplier to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteById(String supplierId) {
        if (supplierId == null || supplierId.trim().isEmpty()) {
            return false;
        }

        List<Supplier> suppliers = findAll();
        boolean removed = suppliers.removeIf(s -> s.getSupplierId().equalsIgnoreCase(supplierId.trim()));

        if (!removed) {
            return false;
        }

        List<String> serializedLines = new ArrayList<>();
        for (Supplier s : suppliers) {
            serializedLines.add(serialize(s));
        }

        return FileUtil.writeAllLines(filePath, serializedLines);
    }

    /**
     * Checks if a supplier exists with the given ID.
     *
     * @param supplierId the supplier ID
     * @return true if found, false otherwise
     */
    public boolean existsById(String supplierId) {
        return findById(supplierId).isPresent();
    }

    /**
     * Serializes a Supplier into a pipe-delimited string line.
     * Format: supplierId|name|contactNumber|email|address
     */
    private String serialize(Supplier supplier) {
        return supplier.getSupplierId() + FileUtil.FIELD_SEPARATOR
                + supplier.getName() + FileUtil.FIELD_SEPARATOR
                + supplier.getContactNumber() + FileUtil.FIELD_SEPARATOR
                + supplier.getEmail() + FileUtil.FIELD_SEPARATOR
                + supplier.getAddress();
    }

    /**
     * Deserializes a pipe-delimited string line into a Supplier object.
     */
    private Supplier deserialize(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = line.split(FileUtil.FIELD_DELIMITER_REGEX, -1);
            if (parts.length < 5) {
                System.err.println("[SupplierRepository Warning] Malformed supplier line skipped (insufficient fields): " + line);
                return null;
            }

            String supplierId = parts[0].trim();
            String name = parts[1].trim();
            String contactNumber = parts[2].trim();
            String email = parts[3].trim();
            String address = parts[4].trim();

            return new Supplier(supplierId, name, contactNumber, email, address);
        } catch (Exception e) {
            System.err.println("[SupplierRepository Warning] Failed to parse supplier record: " + line + " - " + e.getMessage());
            return null;
        }
    }
}
