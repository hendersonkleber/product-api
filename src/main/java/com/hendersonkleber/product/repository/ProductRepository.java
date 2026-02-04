package com.hendersonkleber.product.repository;

import com.hendersonkleber.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE UPPER(p.name) = UPPER(:name) AND p.id <> :id")
    boolean existsByName(String name, Long id);
}
