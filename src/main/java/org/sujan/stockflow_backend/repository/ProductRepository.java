package org.sujan.stockflow_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sujan.stockflow_backend.entity.Product;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findAllByTenantId(Long tenantId);

     Boolean existsByTenantId(Long tenantId);
}
