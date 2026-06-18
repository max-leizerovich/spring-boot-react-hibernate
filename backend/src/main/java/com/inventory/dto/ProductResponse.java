package com.inventory.dto;

import com.inventory.domain.Product;

import java.math.BigDecimal;

/**
 * Product data exposed through the public API.
 *
 * @param id product identifier
 * @param name catalog display name
 * @param sku unique stock-keeping unit
 * @param price unit price
 * @param stockQuantity available inventory count
 * @param active whether the product is visible in the catalog
 */
public record ProductResponse(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        Integer stockQuantity,
        Boolean active) {

    /**
     * Maps a domain product to its API representation.
     *
     * @param product persisted product
     * @return API response DTO
     */
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getActive());
    }
}
