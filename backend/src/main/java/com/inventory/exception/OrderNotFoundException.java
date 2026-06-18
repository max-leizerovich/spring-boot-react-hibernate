package com.inventory.exception;

/**
 * Thrown when an order cannot be found by id.
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Creates an exception for the missing order id.
     *
     * @param id order identifier
     */
    public OrderNotFoundException(Long id) {
        super("Order not found: " + id);
    }
}
