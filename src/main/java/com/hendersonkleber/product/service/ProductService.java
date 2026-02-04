package com.hendersonkleber.product.service;

import com.hendersonkleber.product.dto.PaginatedResponse;
import com.hendersonkleber.product.dto.ProductRequest;
import com.hendersonkleber.product.dto.ProductResponse;
import com.hendersonkleber.product.exception.ResourceAlreadyExistsException;
import com.hendersonkleber.product.exception.ResourceNotFoundException;
import com.hendersonkleber.product.repository.ProductRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return ProductResponse.fromEntity(entity);
    }

    public ProductResponse create(ProductRequest request) {
        if (this.productRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Product with this name already exists");
        }

        var entity = this.productRepository.saveAndFlush(ProductRequest.toEntity(request));

        return ProductResponse.fromEntity(entity);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        if (this.productRepository.existsByName(request.name(), id)) {
            throw new ResourceAlreadyExistsException("Product with this name already exists");
        }

        var entity = this.productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        entity.setName(request.name());
        entity.setPrice(request.price());

        this.productRepository.saveAndFlush(entity);

        return ProductResponse.fromEntity(entity);
    }

    public void delete(Long id) {
        if (!this.productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        this.productRepository.deleteById(id);
    }
}
