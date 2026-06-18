package com.inventory.controller;

import com.inventory.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import com.inventory.domain.Product;
import com.inventory.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for order checkout and read endpoints.
 */
@AutoConfigureMockMvc
@Transactional
class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void checkout_createsOrderAndDecrementsStock() throws Exception {
        String body = """
                {
                  "items": [
                    { "productId": 1, "quantity": 2 },
                    { "productId": 2, "quantity": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.totalAmount", is(149.97)))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].productId").exists())
                .andExpect(jsonPath("$.items[0].quantity").exists())
                .andExpect(jsonPath("$.items[0].unitPrice").exists());

        Product mouse = productRepository.findById(1L).orElseThrow();
        Product keyboard = productRepository.findById(2L).orElseThrow();
        // Seeded stock: mouse 150, keyboard 75
        org.junit.jupiter.api.Assertions.assertEquals(148, mouse.getStockQuantity());
        org.junit.jupiter.api.Assertions.assertEquals(74, keyboard.getStockQuantity());
    }

    @Test
    void checkout_whenInsufficientStock_returnsBadRequest() throws Exception {
        String body = """
                {
                  "items": [
                    { "productId": 1, "quantity": 99999 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("INSUFFICIENT_STOCK")));
    }

    @Test
    void checkout_whenProductMissing_returnsNotFound() throws Exception {
        String body = """
                {
                  "items": [
                    { "productId": 99999, "quantity": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("PRODUCT_NOT_FOUND")));
    }

    @Test
    void checkout_whenProductInactive_returnsBadRequest() throws Exception {
        Product inactive = new Product("Inactive Item", "INACT-001", java.math.BigDecimal.TEN, 5, false);
        inactive = productRepository.save(inactive);

        String body = """
                {
                  "items": [
                    { "productId": %d, "quantity": 1 }
                  ]
                }
                """.formatted(inactive.getId());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("PRODUCT_INACTIVE")));
    }

    @Test
    void checkout_withInvalidBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_FAILED")));
    }

    @Test
    void getOrder_returnsOrderWithItems() throws Exception {
        String body = """
                {
                  "items": [
                    { "productId": 3, "quantity": 1 }
                  ]
                }
                """;

        String response = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int orderId = JsonPath.read(response, "$.id");

        mockMvc.perform(get("/api/v1/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId", is(3)));
    }

    @Test
    void getOrder_whenMissing_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/orders/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("ORDER_NOT_FOUND")));
    }

    @Test
    void listOrders_returnsPaginatedHistory() throws Exception {
        String body = """
                {
                  "items": [
                    { "productId": 4, "quantity": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].status").exists())
                .andExpect(jsonPath("$.content[0].totalAmount").exists());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void checkout_concurrentPurchaseOfLastUnit_allowsOnlyOneSuccess() throws Exception {
        Product scarce = new Product(
                "Scarce Item",
                "SCARCE-" + UUID.randomUUID().toString().substring(0, 8),
                java.math.BigDecimal.valueOf(19.99),
                1,
                true);
        scarce = productRepository.save(scarce);
        Long productId = scarce.getId();

        String body = """
                {
                  "items": [
                    { "productId": %d, "quantity": 1 }
                  ]
                }
                """.formatted(productId);

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger conflictCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    int status = mockMvc.perform(post("/api/v1/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andReturn()
                            .getResponse()
                            .getStatus();
                    if (status == 201) {
                        successCount.incrementAndGet();
                    } else if (status == 409) {
                        conflictCount.incrementAndGet();
                    }
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        org.junit.jupiter.api.Assertions.assertTrue(doneLatch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        org.junit.jupiter.api.Assertions.assertEquals(1, successCount.get());
        org.junit.jupiter.api.Assertions.assertEquals(1, conflictCount.get());

        Product updated = productRepository.findById(productId).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(0, updated.getStockQuantity());
    }
}
