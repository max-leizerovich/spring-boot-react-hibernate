package com.inventory.controller;

import com.inventory.dto.CreateOrderRequest;
import com.inventory.dto.OrderResponse;
import com.inventory.dto.PageResponse;
import com.inventory.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for order checkout and order history reads.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates the controller with its order service dependency.
     *
     * @param orderService order application service
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order from the checkout cart items.
     *
     * @param request validated checkout payload
     * @return created order with line items
     */
    @PostMapping
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse created = orderService.checkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Returns a paginated order history.
     *
     * @param pageable pagination and sort settings
     * @return paginated order summaries
     */
    @GetMapping
    public PageResponse<OrderResponse> listOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.findOrders(pageable);
    }

    /**
     * Returns a single order with its line items.
     *
     * @param id order identifier
     * @return order detail
     */
    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.findById(id);
    }
}
