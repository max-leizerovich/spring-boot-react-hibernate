package com.inventory.repository;

import com.inventory.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence access for customer orders.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
