package org.sujan.stockflow_backend.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sujan.stockflow_backend.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    boolean existsByName(String name);

    @Query("SELECT t.name FROM Tenant t WHERE t.id = :id")
    Optional<String> findNameById(@Param("id") Long id);
}