package com.inventory.util;

import com.inventory.model.Customer;
import com.inventory.model.Product;
import com.inventory.model.Supplier;
import com.inventory.repository.CustomerRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SupplierRepository;

import java.util.List;

/**
 * Utility class to seed initial demo/sample data for manual testing and demonstration.
 * Ensures data is only seeded if the respective repository is currently empty,
 * preventing duplicate records and preserving existing user data.
 */
public class DataSeeder {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public DataSeeder() {
        this(new SupplierRepository(), new ProductRepository(), new CustomerRepository());
    }

    public DataSeeder(SupplierRepository supplierRepository,
                      ProductRepository productRepository,
                      CustomerRepository customerRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Seeds demo suppliers, products, and customers if the corresponding data stores are empty.
     */
    public void seedDemoData() {
        seedSuppliers();
        seedProducts();
        seedCustomers();
    }

    /**
     * Seeds 2 demo suppliers if no suppliers currently exist.
     */
    public void seedSuppliers() {
        if (supplierRepository.findAll().isEmpty()) {
            Supplier s1 = new Supplier("S001", "ABC Supplies", "9876500001", "abc.supplies@gmail.com", "Bhopal");
            Supplier s2 = new Supplier("S002", "City Wholesale", "9876500002", "city.wholesale@gmail.com", "Indore");

            if (!supplierRepository.existsById(s1.getSupplierId())) {
                supplierRepository.save(s1);
            }
            if (!supplierRepository.existsById(s2.getSupplierId())) {
                supplierRepository.save(s2);
            }
        }
    }

    /**
     * Seeds 10 demo products distributed across suppliers if no products currently exist.
     */
    public void seedProducts() {
        if (productRepository.findAll().isEmpty()) {
            // Determine valid supplier IDs from existing repository data
            List<Supplier> existingSuppliers = supplierRepository.findAll();
            String sup1 = "S001";
            String sup2 = "S002";
            if (!existingSuppliers.isEmpty()) {
                sup1 = existingSuppliers.get(0).getSupplierId();
                sup2 = existingSuppliers.size() > 1 ? existingSuppliers.get(1).getSupplierId() : sup1;
            }

            Product[] demoProducts = new Product[]{
                new Product("P001", "Ball Pen", "Stationery", 10.0, 50, 10, sup1),
                new Product("P002", "Notebook", "Stationery", 50.0, 30, 5, sup1),
                new Product("P003", "Pencil Box", "Stationery", 120.0, 15, 5, sup1),
                new Product("P004", "USB Cable", "Electronics", 150.0, 40, 8, sup2),
                new Product("P005", "Computer Mouse", "Electronics", 500.0, 12, 3, sup2),
                new Product("P006", "Keyboard", "Electronics", 900.0, 8, 2, sup2),
                new Product("P007", "Water Bottle", "Accessories", 250.0, 25, 5, sup1),
                new Product("P008", "Backpack", "Accessories", 1200.0, 10, 3, sup1),
                new Product("P009", "Calculator", "Electronics", 350.0, 20, 5, sup2),
                new Product("P010", "Desk Lamp", "Electronics", 700.0, 6, 2, sup2)
            };

            for (Product p : demoProducts) {
                if (!productRepository.existsById(p.getProductId())) {
                    productRepository.save(p);
                }
            }
        }
    }

    /**
     * Seeds 4 demo customers if no customers currently exist.
     */
    public void seedCustomers() {
        if (customerRepository.findAll().isEmpty()) {
            Customer[] demoCustomers = new Customer[]{
                new Customer("C001", "Rahul Sharma", "9876543210", "rahul.demo@gmail.com", "Bhopal"),
                new Customer("C002", "Priya Verma", "9123456780", "priya.demo@gmail.com", "Bhopal"),
                new Customer("C003", "Amit Kumar", "9988776655", "amit.demo@gmail.com", "Indore"),
                new Customer("C004", "Neha Singh", "9090909090", "neha.demo@gmail.com", "Bhopal")
            };

            for (Customer c : demoCustomers) {
                if (!customerRepository.existsById(c.getCustomerId())) {
                    customerRepository.save(c);
                }
            }
        }
    }
}
