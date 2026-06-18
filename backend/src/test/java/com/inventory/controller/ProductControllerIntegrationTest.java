package com.inventory.controller;

import com.inventory.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for product catalog endpoints against the seeded database.
 */
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listProducts_returnsSeededCatalog() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(15))))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(15)))
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].sku").exists())
                .andExpect(jsonPath("$.content[0].price").exists())
                .andExpect(jsonPath("$.content[0].stockQuantity").exists())
                .andExpect(jsonPath("$.content[0].active", is(true)));
    }

    @Test
    void listProducts_searchByName_filtersResults() throws Exception {
        mockMvc.perform(get("/api/v1/products").param("search", "keyboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].sku", is("KB-002")));
    }

    @Test
    void getProduct_returnsSeededProduct() throws Exception {
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.sku", is("WM-001")))
                .andExpect(jsonPath("$.name", is("Wireless Mouse")));
    }

    @Test
    void getProduct_whenMissing_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/products/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("PRODUCT_NOT_FOUND")));
    }

    @Test
    void createProduct_persistsAndReturnsCreated() throws Exception {
        String body = """
                {
                  "name": "Test Gadget",
                  "sku": "TST-999",
                  "price": 9.99,
                  "stockQuantity": 10,
                  "active": true
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku", is("TST-999")))
                .andExpect(jsonPath("$.stockQuantity", is(10)));
    }

    @Test
    void restockProduct_increasesStock() throws Exception {
        String body = """
                {
                  "quantity": 5
                }
                """;

        mockMvc.perform(patch("/api/v1/products/1/restock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(155)));
    }

    @Test
    void createProduct_withInvalidBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "sku": "BAD",
                                  "price": -1,
                                  "stockQuantity": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }
}
