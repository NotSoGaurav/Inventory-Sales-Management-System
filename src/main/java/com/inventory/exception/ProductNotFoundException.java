package com.inventory.exception;

/**
 * Custom checked exception thrown when a requested product cannot be found.
 */
public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
