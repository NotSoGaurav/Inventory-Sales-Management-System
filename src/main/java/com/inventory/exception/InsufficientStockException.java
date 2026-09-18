package com.inventory.exception;

/**
 * Custom checked exception thrown when an attempt is made to reduce or sell stock exceeding available inventory.
 */
public class InsufficientStockException extends Exception {

    private int requestedQuantity;
    private int availableQuantity;

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientStockException(String productId, int requested, int available) {
        super("Insufficient stock for product ID '" + productId + "'. Requested: " + requested + ", Available: " + available);
        this.requestedQuantity = requested;
        this.availableQuantity = available;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
