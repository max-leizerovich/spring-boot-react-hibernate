package com.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Checkout request containing one or more line items.
 *
 * @param items products and quantities to purchase
 */
public record CreateOrderRequest(
        @NotEmpty List<@Valid CreateOrderItemRequest> items) {
}
