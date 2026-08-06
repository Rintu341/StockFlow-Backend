package org.sujan.stockflow_backend.repository;

import org.sujan.stockflow_backend.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    boolean existsByName(String name);
}