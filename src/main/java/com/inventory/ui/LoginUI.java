package com.inventory.ui;

import com.inventory.exception.ValidationException;
import com.inventory.model.User;
import com.inventory.service.AuthenticationService;

import java.util.Scanner;

/**
 * Console UI presentation for user login, credentials prompt, and session launch.
 */
public class LoginUI {

    private final AuthenticationService authService;
    private final Scanner scanner;

    public LoginUI() {
        this(new AuthenticationService(), new Scanner(System.in));
    }

    public LoginUI(AuthenticationService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }

    /**
     * Prompts the user to sign in to the application.
     * Continues until valid credentials are provided or the user types 'exit'.
     *
     * @return the authenticated User, or null if user chose to exit
     */
    public User showLogin() {
        System.out.println("\n==================================================");
        System.out.println("   INVENTORY & SALES MANAGEMENT SYSTEM - LOGIN    ");
        System.out.println("==================================================");
        System.out.println("Default credentials: admin / admin123");
        System.out.println("(Type 'exit' as username to quit)");

        while (true) {
            System.out.print("\nUsername: ");
            String username = scanner.nextLine().trim();

            if (username.equalsIgnoreCase("exit")) {
                System.out.println("Exiting login. Goodbye!");
                return null;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            try {
                User user = authService.login(username, password);
                System.out.println("\n[Success] Welcome, " + user.getFullName() + "! (Role: " + user.getRole() + ")");
                return user;
            } catch (ValidationException e) {
                System.out.println("[Login Failed] " + e.getMessage());
                System.out.println("Please try again or enter 'exit' to quit.");
            }
        }
    }

    /**
     * Logs out the current user session.
     */
    public void logout() {
        if (authService.isLoggedIn()) {
            User current = authService.getCurrentUser();
            System.out.println("\nLogging out user: " + current.getUsername() + " (" + current.getFullName() + ")");
            authService.logout();
            System.out.println("[Success] You have been logged out successfully.");
        }
    }

    public AuthenticationService getAuthService() {
        return authService;
    }
}
