package com.inventory.repository;

import com.inventory.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Persistence access for catalog products.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Returns a filtered, paginated product catalog.
     *
     * @param active optional active flag filter; {@code null} matches all
     * @param search optional case-insensitive name or SKU substring
     * @param pageable pagination and sort
     * @return matching products page
     */
    @Query("""
            SELECT p FROM Product p
            WHERE (:active IS NULL OR p.active = :active)
              AND (
                :search IS NULL OR :search = ''
                OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))
              )
            """)
    Page<Product> searchProducts(
            @Param("active") Boolean active,
            @Param("search") String search,
            Pageable pageable);
}
