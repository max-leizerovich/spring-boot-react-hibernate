package com.inventory.service;

import com.inventory.domain.Product;
import com.inventory.dto.CreateProductRequest;
import com.inventory.dto.PageResponse;
import com.inventory.dto.ProductResponse;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Product catalog operations with read caching and write cache eviction.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Creates the service with its product repository dependency.
     *
     * @param productRepository product persistence layer
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Returns a filtered, paginated product catalog.
     *
     * @param active optional active flag filter
     * @param search optional name or SKU search term
     * @param pageable pagination settings
     * @return paginated product responses
     */
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "products",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort + '-' + #search + '-' + #active")
    public PageResponse<ProductResponse> findProducts(Boolean active, String search, Pageable pageable) {
        Page<Product> page = productRepository.searchProducts(active, normalizeSearch(search), pageable);
        return PageResponse.from(page, ProductResponse::from);
    }

    /**
     * Returns a single product by id.
     *
     * @param id product identifier
     * @return product response
     * @throws ProductNotFoundException when the product does not exist
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "product", key = "#id")
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * Creates a new catalog product.
     *
     * @param request create payload
     * @return created product response
     */
    @Transactional
    @CacheEvict(cacheNames = {"products", "product"}, allEntries = true)
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product(
                request.name(),
                request.sku(),
                request.price(),
                request.stockQuantity(),
                request.active());
        return ProductResponse.from(productRepository.save(product));
    }

    /**
     * Adds stock to an existing product.
     *
     * @param id product identifier
     * @param quantity units to add
     * @return updated product response
     * @throws ProductNotFoundException when the product does not exist
     */
    @Transactional
    @CacheEvict(cacheNames = {"products", "product"}, allEntries = true)
    public ProductResponse restock(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.restock(quantity);
        return ProductResponse.from(productRepository.save(product));
    }

    private String normalizeSearch(String search) {
        if (search == null) {
            return null;
        }
        String trimmed = search.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
