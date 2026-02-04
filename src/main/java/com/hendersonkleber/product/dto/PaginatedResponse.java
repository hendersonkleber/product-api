package com.hendersonkleber.product.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> content,
        int totalPages,
        long totalItems
) {
}
