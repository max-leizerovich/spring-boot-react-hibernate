package com.inventory.service;

import com.inventory.domain.Order;
import com.inventory.domain.OrderItem;
import com.inventory.domain.OrderStatus;
import com.inventory.domain.Product;
import com.inventory.dto.CreateOrderItemRequest;
import com.inventory.dto.CreateOrderRequest;
import com.inventory.dto.OrderResponse;
import com.inventory.dto.PageResponse;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.OrderNotFoundException;
import com.inventory.exception.ProductInactiveException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.repository.OrderRepository;
import com.inventory.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Order checkout and read operations with transactional stock updates.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * Creates the service with its repository dependencies.
     *
     * @param orderRepository order persistence layer
     * @param productRepository product persistence layer
     */
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Returns a paginated order history sorted by the caller's pageable settings.
     *
     * @param pageable pagination and sort settings
     * @return paginated order summaries without line items
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> findOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return PageResponse.from(page, OrderResponse::fromSummary);
    }

    /**
     * Returns a single order with its line items.
     *
     * @param id order identifier
     * @return order detail
     * @throws OrderNotFoundException when the order does not exist
     */
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        initializeItems(order);
        return OrderResponse.fromDetail(order);
    }

    /**
     * Validates stock, decrements inventory, and creates a confirmed order atomically.
     *
     * @param request checkout payload
     * @return created order with line items
     * @throws ProductNotFoundException when a product id does not exist
     * @throws ProductInactiveException when a product is not active
     * @throws InsufficientStockException when requested quantity exceeds available stock
     */
    @Transactional
    @CacheEvict(cacheNames = {"products", "product"}, allEntries = true)
    public OrderResponse checkout(CreateOrderRequest request) {
        Map<Long, Integer> quantitiesByProductId = aggregateQuantities(request.items());
        Map<Long, Product> productsById = loadProducts(quantitiesByProductId);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> lineItems = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : quantitiesByProductId.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = productsById.get(productId);

            validateProductForCheckout(product, productId, quantity);

            BigDecimal unitPrice = product.getPrice();
            totalAmount = totalAmount.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
            lineItems.add(new OrderItem(product, quantity, unitPrice));

            product.decrementStock(quantity);
            productRepository.save(product);
        }

        Order order = new Order(OrderStatus.CONFIRMED, totalAmount);
        for (OrderItem lineItem : lineItems) {
            order.addItem(lineItem);
        }

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.fromDetail(savedOrder);
    }

    private Map<Long, Integer> aggregateQuantities(List<CreateOrderItemRequest> items) {
        Map<Long, Integer> quantitiesByProductId = new HashMap<>();
        for (CreateOrderItemRequest item : items) {
            quantitiesByProductId.merge(item.productId(), item.quantity(), Integer::sum);
        }
        return quantitiesByProductId;
    }

    private Map<Long, Product> loadProducts(Map<Long, Integer> quantitiesByProductId) {
        List<Long> productIds = List.copyOf(quantitiesByProductId.keySet());
        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            Long missingId = productIds.stream()
                    .filter(id -> products.stream().noneMatch(product -> product.getId().equals(id)))
                    .findFirst()
                    .orElseThrow();
            throw new ProductNotFoundException(missingId);
        }

        Map<Long, Product> productsById = new HashMap<>();
        for (Product product : products) {
            productsById.put(product.getId(), product);
        }
        return productsById;
    }

    private void validateProductForCheckout(Product product, Long productId, int quantity) {
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ProductInactiveException(productId);
        }
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStockQuantity());
        }
    }

    private void initializeItems(Order order) {
        order.getItems().size();
    }
}
