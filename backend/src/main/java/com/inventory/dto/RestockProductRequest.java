package com.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for adding stock to an existing product.
 *
 * @param quantity units to add to current stock
 */
public record RestockProductRequest(
        @NotNull @Min(1) Integer quantity) {
}
