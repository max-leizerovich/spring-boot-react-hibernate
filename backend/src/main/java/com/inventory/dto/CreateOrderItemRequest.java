package com.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Single line item in a checkout request.
 *
 * @param productId catalog product identifier
 * @param quantity units to purchase
 */
public record CreateOrderItemRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity) {
}
