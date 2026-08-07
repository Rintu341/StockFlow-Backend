package org.sujan.stockflow_backend.repository;

import org.sujan.stockflow_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByTenantId(Long tenantId);
}