package com.inventory.validation;

import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.util.InputValidator;

/**
 * Unit tests for input validation rules, numeric parsing, and custom exceptions.
 */
public class ValidationTest {

    public static int runTests() {
        System.out.println("\n--- Running Validation & Exception Tests ---");
        int passed = 0;

        // 1. Text & Delimiter validation
        try {
            InputValidator.validateRequiredText("Clean Text", "Name");
            passed++;
            System.out.println("  [PASS] Clean required text validated");
        } catch (ValidationException e) {
            System.err.println("  [FAIL] " + e.getMessage());
        }

        try {
            InputValidator.validateNoDelimiters("Text|With|Pipe", "Field");
            System.err.println("  [FAIL] Should reject pipe delimiter");
        } catch (ValidationException e) {
            passed++;
            System.out.println("  [PASS] Delimiter injection prevented");
        }

        // 2. Integer parsing
        try {
            int qty = InputValidator.parsePositiveInt("42", "Quantity");
            if (qty == 42) passed++;
            System.out.println("  [PASS] Positive integer parsed");
        } catch (ValidationException e) {
            System.err.println("  [FAIL] " + e.getMessage());
        }

        try {
            InputValidator.parsePositiveInt("-5", "Quantity");
            System.err.println("  [FAIL] Should reject negative integer");
        } catch (ValidationException e) {
            passed++;
            System.out.println("  [PASS] Negative integer rejected");
        }

        // 3. Price parsing
        try {
            double price = InputValidator.parsePositiveDouble("99.95", "Price");
            if (price == 99.95) passed++;
            System.out.println("  [PASS] Positive double parsed");
        } catch (ValidationException e) {
            System.err.println("  [FAIL] " + e.getMessage());
        }

        // 4. Email validation
        if (InputValidator.isValidEmail("user@example.com") && !InputValidator.isValidEmail("invalid-email")) {
            passed++;
            System.out.println("  [PASS] Email validation rules");
        }

        // 5. Phone validation
        if (InputValidator.isValidPhone("9876543210") && !InputValidator.isValidPhone("123")) {
            passed++;
            System.out.println("  [PASS] Phone validation rules");
        }

        // 6. Custom Exception verification
        InsufficientStockException stockEx = new InsufficientStockException("P101", 10, 2);
        if (stockEx.getRequestedQuantity() == 10 && stockEx.getAvailableQuantity() == 2) {
            passed++;
            System.out.println("  [PASS] InsufficientStockException detail getters");
        }

        ProductNotFoundException notFoundEx = new ProductNotFoundException("Not found");
        if (notFoundEx.getMessage().equals("Not found")) {
            passed++;
            System.out.println("  [PASS] ProductNotFoundException message handling");
        }

        return passed;
    }
}
