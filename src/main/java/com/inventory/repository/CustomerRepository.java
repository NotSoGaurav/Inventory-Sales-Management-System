package com.inventory.repository;

import com.inventory.model.Customer;
import com.inventory.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File-based repository for persisting and retrieving Customer entities.
 */
public class CustomerRepository {

    private final String filePath;

    public CustomerRepository() {
        this("data/customers.txt");
    }

    public CustomerRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves a new customer record to file.
     *
     * @param customer the customer to save
     * @return true if saved, false if customer is null or ID already exists
     */
    public boolean save(Customer customer) {
        if (customer == null || customer.getCustomerId() == null || customer.getCustomerId().trim().isEmpty()) {
            return false;
        }

        if (existsById(customer.getCustomerId())) {
            System.err.println("[CustomerRepository Warning] Customer with ID '" + customer.getCustomerId() + "' already exists.");
            return false;
        }

        String serialized = serialize(customer);
        return FileUtil.appendLine(filePath, serialized);
    }

    /**
     * Finds a customer by customer ID.
     *
     * @param customerId the customer ID
     * @return an Optional containing the found customer, or empty if not found
     */
    public Optional<Customer> findById(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return Optional.empty();
        }

        return findAll().stream()
                .filter(c -> c.getCustomerId().equalsIgnoreCase(customerId.trim()))
                .findFirst();
    }

    /**
     * Retrieves all customers from the file.
     *
     * @return list of all valid customers
     */
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        List<String> lines = FileUtil.readAllLines(filePath);

        for (String line : lines) {
            Customer customer = deserialize(line);
            if (customer != null) {
                customers.add(customer);
            }
        }

        return customers;
    }

    /**
     * Updates an existing customer record.
     *
     * @param updatedCustomer the customer with updated data
     * @return true if updated, false if customer is null or not found
     */
    public boolean update(Customer updatedCustomer) {
        if (updatedCustomer == null || updatedCustomer.getCustomerId() == null) {
            return false;
        }

        List<Customer> customers = findAll();
        boolean found = false;

        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getCustomerId().equalsIgnoreCase(updatedCustomer.getCustomerId().trim())) {
                customers.set(i, updatedCustomer);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        List<String> serializedLines = new ArrayList<>();
        for (Customer c : customers) {
            serializedLines.add(serialize(c));
        }

        return FileUtil.writeAllLines(filePath, serializedLines);
    }

    /**
     * Deletes a customer by customer ID.
     *
     * @param customerId the ID of the customer to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteById(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return false;
        }

        List<Customer> customers = findAll();
        boolean removed = customers.removeIf(c -> c.getCustomerId().equalsIgnoreCase(customerId.trim()));

        if (!removed) {
            return false;
        }

        List<String> serializedLines = new ArrayList<>();
        for (Customer c : customers) {
            serializedLines.add(serialize(c));
        }

        return FileUtil.writeAllLines(filePath, serializedLines);
    }

    /**
     * Checks if a customer exists with the given ID.
     *
     * @param customerId the customer ID
     * @return true if found, false otherwise
     */
    public boolean existsById(String customerId) {
        return findById(customerId).isPresent();
    }

    /**
     * Serializes a Customer into a pipe-delimited string line.
     * Format: customerId|name|phone|email|address
     */
    private String serialize(Customer customer) {
        return customer.getCustomerId() + FileUtil.FIELD_SEPARATOR
                + customer.getName() + FileUtil.FIELD_SEPARATOR
                + customer.getPhone() + FileUtil.FIELD_SEPARATOR
                + customer.getEmail() + FileUtil.FIELD_SEPARATOR
                + customer.getAddress();
    }

    /**
     * Deserializes a pipe-delimited string line into a Customer object.
     */
    private Customer deserialize(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = line.split(FileUtil.FIELD_DELIMITER_REGEX, -1);
            if (parts.length < 5) {
                System.err.println("[CustomerRepository Warning] Malformed customer line skipped (insufficient fields): " + line);
                return null;
            }

            String customerId = parts[0].trim();
            String name = parts[1].trim();
            String phone = parts[2].trim();
            String email = parts[3].trim();
            String address = parts[4].trim();

            return new Customer(customerId, name, phone, email, address);
        } catch (Exception e) {
            System.err.println("[CustomerRepository Warning] Failed to parse customer record: " + line + " - " + e.getMessage());
            return null;
        }
    }
}
