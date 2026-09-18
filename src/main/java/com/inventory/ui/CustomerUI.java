package com.inventory.ui;

import com.inventory.exception.ValidationException;
import com.inventory.model.Customer;
import com.inventory.service.CustomerService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console UI layer for Customer Management.
 * Handles menu options, user input prompting, and customer record formatting.
 */
public class CustomerUI {

    private final CustomerService customerService;
    private final Scanner scanner;

    public CustomerUI() {
        this(new CustomerService(), new Scanner(System.in));
    }

    public CustomerUI(CustomerService customerService, Scanner scanner) {
        this.customerService = customerService;
        this.scanner = scanner;
    }

    /**
     * Displays the Customer Management menu loop.
     */
    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n==================================================");
            System.out.println("              CUSTOMER MANAGEMENT                 ");
            System.out.println("==================================================");
            System.out.println(" 1. Add New Customer");
            System.out.println(" 2. View All Customers");
            System.out.println(" 3. Search Customers");
            System.out.println(" 4. Find Customer by ID");
            System.out.println(" 5. Update Customer");
            System.out.println(" 6. Delete Customer");
            System.out.println(" 7. Back to Main Menu");
            System.out.println("==================================================");
            System.out.print("Enter your choice (1-7): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleAddCustomer();
                    break;
                case "2":
                    handleViewAllCustomers();
                    break;
                case "3":
                    handleSearchCustomers();
                    break;
                case "4":
                    handleFindById();
                    break;
                case "5":
                    handleUpdateCustomer();
                    break;
                case "6":
                    handleDeleteCustomer();
                    break;
                case "7":
                    running = false;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("[Error] Invalid option. Please enter a number between 1 and 7.");
            }
        }
    }

    private void handleAddCustomer() {
        System.out.println("\n--- Add New Customer ---");
        try {
            System.out.print("Enter Customer ID (e.g. C101): ");
            String id = scanner.nextLine().trim();

            System.out.print("Enter Customer Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Phone Number (10 digits): ");
            String phone = scanner.nextLine().trim();

            System.out.print("Enter Email Address (optional, press Enter to skip): ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter Address: ");
            String address = scanner.nextLine().trim();

            Customer customer = new Customer(id, name, phone, email, address);
            customerService.addCustomer(customer);

            System.out.println("[Success] Customer '" + name + "' added successfully with ID: " + id);
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Error] Failed to add customer: " + e.getMessage());
        }
    }

    private void handleViewAllCustomers() {
        System.out.println("\n--- All Customers ---");
        List<Customer> customers = customerService.getAllCustomers();
        displayCustomerTable(customers);
    }

    private void handleSearchCustomers() {
        System.out.println("\n--- Search Customers ---");
        System.out.print("Enter search keyword (name, phone, email, or ID): ");
        String query = scanner.nextLine().trim();

        List<Customer> results = customerService.searchCustomers(query);
        displayCustomerTable(results);
    }

    private void handleFindById() {
        System.out.println("\n--- Find Customer by ID ---");
        System.out.print("Enter Customer ID: ");
        String id = scanner.nextLine().trim();

        try {
            Optional<Customer> opt = customerService.getCustomerById(id);
            if (opt.isPresent()) {
                displayCustomerCard(opt.get());
            } else {
                System.out.println("[Not Found] Customer with ID '" + id + "' was not found.");
            }
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void handleUpdateCustomer() {
        System.out.println("\n--- Update Customer ---");
        System.out.print("Enter Customer ID to update: ");
        String id = scanner.nextLine().trim();

        try {
            Optional<Customer> opt = customerService.getCustomerById(id);
            if (!opt.isPresent()) {
                System.out.println("[Not Found] Customer with ID '" + id + "' was not found.");
                return;
            }

            Customer existing = opt.get();
            System.out.println("Customer found: " + existing.getName());
            System.out.println("(Press Enter to keep current value)");

            System.out.print("New Name [" + existing.getName() + "]: ");
            String nameInput = scanner.nextLine().trim();
            String name = nameInput.isEmpty() ? existing.getName() : nameInput;

            System.out.print("New Phone [" + existing.getPhone() + "]: ");
            String phoneInput = scanner.nextLine().trim();
            String phone = phoneInput.isEmpty() ? existing.getPhone() : phoneInput;

            System.out.print("New Email [" + existing.getEmail() + "]: ");
            String emailInput = scanner.nextLine().trim();
            String email = emailInput.isEmpty() ? existing.getEmail() : emailInput;

            System.out.print("New Address [" + existing.getAddress() + "]: ");
            String addressInput = scanner.nextLine().trim();
            String address = addressInput.isEmpty() ? existing.getAddress() : addressInput;

            Customer updated = new Customer(existing.getCustomerId(), name, phone, email, address);
            customerService.updateCustomer(updated);

            System.out.println("[Success] Customer updated successfully!");
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void handleDeleteCustomer() {
        System.out.println("\n--- Delete Customer ---");
        System.out.print("Enter Customer ID to delete: ");
        String id = scanner.nextLine().trim();

        try {
            Optional<Customer> opt = customerService.getCustomerById(id);
            if (!opt.isPresent()) {
                System.out.println("[Not Found] Customer with ID '" + id + "' was not found.");
                return;
            }

            Customer customer = opt.get();
            System.out.print("Are you sure you want to delete customer '" + customer.getName() + "' (" + id + ")? (y/n): ");
            String confirm = scanner.nextLine().trim();

            if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
                customerService.deleteCustomer(id);
                System.out.println("[Success] Customer deleted successfully.");
            } else {
                System.out.println("[Cancelled] Deletion cancelled.");
            }
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private void displayCustomerTable(List<Customer> customers) {
        if (customers == null || customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-20s | %-15s | %-25s | %-20s%n",
                "ID", "Name", "Phone", "Email", "Address");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        for (Customer c : customers) {
            System.out.printf("%-10s | %-20s | %-15s | %-25s | %-20s%n",
                    c.getCustomerId(),
                    truncate(c.getName(), 20),
                    c.getPhone(),
                    truncate(c.getEmail() != null && !c.getEmail().isEmpty() ? c.getEmail() : "-", 25),
                    truncate(c.getAddress(), 20));
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println("Total customers: " + customers.size());
    }

    private void displayCustomerCard(Customer c) {
        System.out.println("\n==========================================");
        System.out.println("             CUSTOMER DETAILS             ");
        System.out.println("==========================================");
        System.out.println(" Customer ID : " + c.getCustomerId());
        System.out.println(" Name        : " + c.getName());
        System.out.println(" Phone       : " + c.getPhone());
        System.out.println(" Email       : " + (c.getEmail() != null && !c.getEmail().isEmpty() ? c.getEmail() : "None"));
        System.out.println(" Address     : " + c.getAddress());
        System.out.println("==========================================");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
