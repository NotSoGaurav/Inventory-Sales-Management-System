package com.inventory.ui;

import com.inventory.model.User;
import com.inventory.service.AuthenticationService;
import com.inventory.service.CustomerService;
import com.inventory.service.InventoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SalesService;

import java.util.Scanner;

/**
 * Main application menu controller and overall workflow orchestrator.
 * Routes user choices to specific feature modules and handles user session lifecycle.
 */
public class MainMenuUI {

    private final LoginUI loginUI;
    private final ProductUI productUI;
    private final InventoryUI inventoryUI;
    private final CustomerUI customerUI;
    private final SalesUI salesUI;
    private final ReportUI reportUI;
    private final Scanner scanner;

    public MainMenuUI() {
        this(new Scanner(System.in));
    }

    public MainMenuUI(Scanner scanner) {
        this.scanner = scanner;
        this.loginUI = new LoginUI(new AuthenticationService(), scanner);
        this.productUI = new ProductUI(new ProductService(), scanner);
        this.inventoryUI = new InventoryUI(new InventoryService(), scanner);
        this.customerUI = new CustomerUI(new CustomerService(), scanner);
        this.salesUI = new SalesUI(new SalesService(), scanner);
        this.reportUI = new ReportUI(scanner);
    }

    public MainMenuUI(LoginUI loginUI, ProductUI productUI, InventoryUI inventoryUI,
                      CustomerUI customerUI, SalesUI salesUI, ReportUI reportUI, Scanner scanner) {
        this.loginUI = loginUI;
        this.productUI = productUI;
        this.inventoryUI = inventoryUI;
        this.customerUI = customerUI;
        this.salesUI = salesUI;
        this.reportUI = reportUI;
        this.scanner = scanner;
    }

    /**
     * Starts the main application lifecycle: Login -> Main Menu -> Sub-menus -> Logout / Exit.
     */
    public void start() {
        boolean appRunning = true;

        System.out.println("=========================================================");
        System.out.println("     WELCOME TO INVENTORY & SALES MANAGEMENT SYSTEM      ");
        System.out.println("=========================================================");

        while (appRunning) {
            User user = loginUI.showLogin();
            if (user == null) {
                appRunning = false;
                System.out.println("\nExiting application. Thank you for using the system!");
                break;
            }

            boolean sessionActive = true;
            while (sessionActive) {
                displayMenu(user);
                System.out.print("Enter your choice (1-7): ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        productUI.showMenu();
                        break;
                    case "2":
                        inventoryUI.showMenu();
                        break;
                    case "3":
                        customerUI.showMenu();
                        break;
                    case "4":
                        salesUI.showMenu();
                        break;
                    case "5":
                        reportUI.showMenu();
                        break;
                    case "6":
                        loginUI.logout();
                        sessionActive = false;
                        break;
                    case "7":
                        loginUI.logout();
                        sessionActive = false;
                        appRunning = false;
                        System.out.println("\nExiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("[Error] Invalid option. Please enter a number between 1 and 7.");
                }
            }
        }
    }

    private void displayMenu(User user) {
        System.out.println("\n==================================================");
        System.out.println("                   MAIN MENU                      ");
        System.out.println("==================================================");
        System.out.printf(" Logged in as: %s [Role: %s]%n", user.getFullName(), user.getRole());
        System.out.println("==================================================");
        System.out.println(" 1. Product Management");
        System.out.println(" 2. Inventory & Stock Management");
        System.out.println(" 3. Customer Management");
        System.out.println(" 4. Sales & Billing");
        System.out.println(" 5. Reports & Analytics");
        System.out.println(" 6. Logout");
        System.out.println(" 7. Exit Application");
        System.out.println("==================================================");
    }
}
