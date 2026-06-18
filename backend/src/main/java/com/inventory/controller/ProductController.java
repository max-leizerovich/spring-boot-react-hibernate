package com.inventory.controller;

import com.inventory.dto.CreateProductRequest;
import com.inventory.dto.PageResponse;
import com.inventory.dto.ProductResponse;
import com.inventory.dto.RestockProductRequest;
import com.inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for catalog product reads and admin-style mutations.
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Creates the controller with its product service dependency.
     *
     * @param productService product application service
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Returns a paginated, searchable product catalog.
     *
     * @param active optional active filter
     * @param search optional name or SKU search term
     * @param pageable pagination and sort settings
     * @return paginated products
     */
    @GetMapping
    public PageResponse<ProductResponse> listProducts(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return productService.findProducts(active, search, pageable);
    }

    /**
     * Returns a single product by id.
     *
     * @param id product identifier
     * @return product detail
     */
    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }

    /**
     * Creates a new catalog product.
     *
     * @param request validated create payload
     * @return created product
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Adds stock to an existing product.
     *
     * @param id product identifier
     * @param request validated restock payload
     * @return updated product
     */
    @PatchMapping("/{id}/restock")
    public ProductResponse restockProduct(
            @PathVariable Long id,
            @Valid @RequestBody RestockProductRequest request) {
        return productService.restock(id, request.quantity());
    }
}
