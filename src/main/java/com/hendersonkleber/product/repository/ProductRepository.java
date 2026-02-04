package com.hendersonkleber.product.repository;

import com.hendersonkleber.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
