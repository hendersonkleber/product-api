package com.hendersonkleber.product.service;

import com.hendersonkleber.product.dto.PaginatedResponse;
import com.hendersonkleber.product.dto.ProductRequest;
import com.hendersonkleber.product.dto.ProductResponse;
import com.hendersonkleber.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public PaginatedResponse<ProductResponse> getAll(
            int page,
            int limit,
            String sort,
            String order
    ) {
        var pageRequest = PageRequest.of(
                page,
                limit,
                order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sort
        );

        var pageResponse = this.productRepository.findAll(pageRequest);

        return new PaginatedResponse<>(
                pageResponse.getContent().stream().map(ProductResponse::fromEntity).toList(),
                pageResponse.getTotalPages(),
                pageResponse.getTotalElements()
        );
    }

    public ProductResponse getById(Long id) {
        var entity = this.productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));

        return ProductResponse.fromEntity(entity);
    }

    public ProductResponse create(ProductRequest request) {
        if (this.productRepository.existsByName(request.name())) {
            throw new RuntimeException("Product already exists with this name");
        }

        var entity = this.productRepository.saveAndFlush(ProductRequest.toEntity(request));

        return ProductResponse.fromEntity(entity);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        if (this.productRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new RuntimeException("Product already exists with this name");
        }

        var entity = this.productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));

        entity.setName(request.name());
        entity.setPrice(request.price());

        this.productRepository.saveAndFlush(entity);

        return ProductResponse.fromEntity(entity);
    }

    public void delete(Long id) {
        if (!this.productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product does not exist");
        }

        this.productRepository.deleteById(id);
    }
}
