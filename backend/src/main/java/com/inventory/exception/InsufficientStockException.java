package com.inventory.exception;

/**
 * Thrown when checkout requests more units than are available in stock.
 */
public class InsufficientStockException extends RuntimeException {

    /**
     * Creates an exception for insufficient product stock.
     *
     * @param productId product identifier
     * @param requested units requested
     * @param available units currently in stock
     */
    public InsufficientStockException(Long productId, int requested, int available) {
        super("Insufficient stock for product " + productId + ": requested " + requested + ", available " + available);
    }
}
