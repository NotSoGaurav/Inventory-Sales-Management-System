package com.inventory.service;

import com.inventory.exception.ValidationException;
import com.inventory.model.Customer;
import com.inventory.repository.CustomerRepository;
import com.inventory.util.InputValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class encapsulating business logic, validations, and operations for Customer management.
 */
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService() {
        this(new CustomerRepository());
    }

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Adds a new customer after performing field and uniqueness validation.
     *
     * @param customer the customer to add
     * @return the saved customer
     * @throws ValidationException if validation fails or customer ID already exists
     */
    public Customer addCustomer(Customer customer) throws ValidationException {
        if (customer == null) {
            throw new ValidationException("Customer cannot be null.");
        }

        InputValidator.validateId(customer.getCustomerId(), "Customer ID");
        InputValidator.validateRequiredText(customer.getName(), "Customer name");
        InputValidator.validatePhone(customer.getPhone(), true);
        InputValidator.validateEmail(customer.getEmail(), false);
        InputValidator.validateRequiredText(customer.getAddress(), "Customer address");

        if (customerRepository.existsById(customer.getCustomerId())) {
            throw new ValidationException("Customer with ID '" + customer.getCustomerId() + "' already exists.");
        }

        boolean saved = customerRepository.save(customer);
        if (!saved) {
            throw new ValidationException("Failed to persist customer to storage.");
        }

        return customer;
    }

    /**
     * Retrieves all customers.
     *
     * @return list of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Finds a customer by ID.
     *
     * @param customerId the customer ID
     * @return an Optional containing the found customer
     * @throws ValidationException if customer ID is blank
     */
    public Optional<Customer> getCustomerById(String customerId) throws ValidationException {
        InputValidator.validateId(customerId, "Customer ID");
        return customerRepository.findById(customerId);
    }

    /**
     * Searches customers matching name, phone, email, or ID (case-insensitive substring search).
     *
     * @param query search keyword
     * @return list of matching customers
     */
    public List<Customer> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCustomers();
        }

        String lowerQuery = query.trim().toLowerCase();
        return customerRepository.findAll().stream()
                .filter(c -> (c.getCustomerId() != null && c.getCustomerId().toLowerCase().contains(lowerQuery)) ||
                        (c.getName() != null && c.getName().toLowerCase().contains(lowerQuery)) ||
                        (c.getPhone() != null && c.getPhone().contains(lowerQuery)) ||
                        (c.getEmail() != null && c.getEmail().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing customer record.
     *
     * @param customer updated customer details
     * @return true if updated successfully
     * @throws ValidationException if customer does not exist or validation rules fail
     */
    public boolean updateCustomer(Customer customer) throws ValidationException {
        if (customer == null) {
            throw new ValidationException("Customer cannot be null.");
        }

        InputValidator.validateId(customer.getCustomerId(), "Customer ID");
        InputValidator.validateRequiredText(customer.getName(), "Customer name");
        InputValidator.validatePhone(customer.getPhone(), true);
        InputValidator.validateEmail(customer.getEmail(), false);
        InputValidator.validateRequiredText(customer.getAddress(), "Customer address");

        if (!customerRepository.existsById(customer.getCustomerId())) {
            throw new ValidationException("Cannot update. Customer with ID '" + customer.getCustomerId() + "' does not exist.");
        }

        return customerRepository.update(customer);
    }

    /**
     * Deletes a customer by ID.
     *
     * @param customerId the customer ID to delete
     * @return true if deleted successfully
     * @throws ValidationException if customer does not exist or ID is blank
     */
    public boolean deleteCustomer(String customerId) throws ValidationException {
        InputValidator.validateId(customerId, "Customer ID");

        if (!customerRepository.existsById(customerId)) {
            throw new ValidationException("Cannot delete. Customer with ID '" + customerId + "' does not exist.");
        }

        return customerRepository.deleteById(customerId);
    }

    /**
     * Checks if a customer exists with the given ID.
     *
     * @param customerId the customer ID
     * @return true if customer exists, false otherwise
     */
    public boolean existsById(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return false;
        }
        return customerRepository.existsById(customerId.trim());
    }
}
