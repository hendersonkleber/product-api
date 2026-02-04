package com.hendersonkleber.product.dto;

import com.hendersonkleber.product.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        Long id,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 120, message = "Name must be between 2 and 120 characters")
        String name,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be a positive value")
        BigDecimal price
) {
    public static Product toEntity(ProductRequest request) {
        var entity = new Product();

        if (request.id() != null && request.id() > 0) {
            entity.setId(request.id());
        }

        entity.setName(request.name());
        entity.setPrice(request.price());

        return entity;
    }
}
