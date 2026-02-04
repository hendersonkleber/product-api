package com.hendersonkleber.product.dto;

import com.hendersonkleber.product.domain.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price
) {
    public static ProductResponse fromEntity(Product entity) {
        return new ProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getPrice()
        );
    }
}
