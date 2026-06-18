package com.inventory.dto;

import com.inventory.domain.Order;
import com.inventory.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Order summary returned by the API.
 *
 * @param id order identifier
 * @param status order lifecycle status
 * @param totalAmount computed order total
 * @param createdAt creation timestamp
 * @param items line items when included in the response
 */
public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        List<OrderItemResponse> items) {

    /**
     * Maps a persisted order to the API response shape without line items.
     *
     * @param order source entity
     * @return API response without items
     */
    public static OrderResponse fromSummary(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                null);
    }

    /**
     * Maps a persisted order to the API response shape with line items.
     *
     * @param order source entity
     * @return API response with items
     */
    public static OrderResponse fromDetail(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items);
    }
}
