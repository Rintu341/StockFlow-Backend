package org.sujan.stockflow_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sujan.stockflow_backend.entity.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

}
