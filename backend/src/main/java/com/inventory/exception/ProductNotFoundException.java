package com.inventory.exception;

/**
 * Raised when a product identifier does not exist.
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Creates an exception for the given product id.
     *
     * @param id missing product identifier
     */
    public ProductNotFoundException(Long id) {
        super("Product not found: " + id);
    }
}
