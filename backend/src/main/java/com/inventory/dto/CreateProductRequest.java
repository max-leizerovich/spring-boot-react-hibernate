package com.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request body for creating a catalog product.
 *
 * @param name catalog display name
 * @param sku unique stock-keeping unit
 * @param price unit price
 * @param stockQuantity initial inventory count
 * @param active whether the product is visible in the catalog
 */
public record CreateProductRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 50) String sku,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        @NotNull @Min(0) Integer stockQuantity,
        Boolean active) {
}
