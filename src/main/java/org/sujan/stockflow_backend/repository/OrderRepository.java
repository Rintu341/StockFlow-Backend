package org.sujan.stockflow_backend.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sujan.stockflow_backend.dto.RecentOrderDto;
import org.sujan.stockflow_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.sujan.stockflow_backend.entity.OrderStatus;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByTenantId(Long tenantId);

    long countByTenantIdAndStatus(Long tenantId, OrderStatus status);

    @Query("""
    select new org.sujan.stockflow_backend.dto.RecentOrderDto(
        o.id, o.customerName, size(o.items), o.createdAt, o.status)
    from org.sujan.stockflow_backend.entity.Order o
    where o.tenantId = :tenantId
    order by o.createdAt desc, o.id desc
    """)
    List<RecentOrderDto> findRecent(@Param("tenantId") Long tenantId, Pageable pageable);
}