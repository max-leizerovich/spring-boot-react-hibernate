package com.inventory.dto;

import com.inventory.domain.Order;
import com.inventory.domain.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Order line item returned by the API.
 *
 * @param id line item identifier
 * @param productId purchased product identifier
 * @param quantity units ordered
 * @param unitPrice price per unit at checkout time
 */
public record OrderItemResponse(
        Long id,
        Long productId,
        Integer quantity,
        BigDecimal unitPrice) {

    /**
     * Maps a persisted order item to the API response shape.
     *
     * @param item source entity
     * @return API response
     */
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getQuantity(),
                item.getUnitPrice());
    }
}
