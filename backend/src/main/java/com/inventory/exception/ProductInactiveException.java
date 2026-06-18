package com.inventory.exception;

/**
 * Thrown when checkout includes a product that is not active in the catalog.
 */
public class ProductInactiveException extends RuntimeException {

    /**
     * Creates an exception for an inactive product.
     *
     * @param productId product identifier
     */
    public ProductInactiveException(Long productId) {
        super("Product is not active: " + productId);
    }
}
