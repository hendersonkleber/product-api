package com.hendersonkleber.product.service;

import com.hendersonkleber.product.dto.PaginatedResponse;
import com.hendersonkleber.product.dto.ProductRequest;
import com.hendersonkleber.product.dto.ProductResponse;
import com.hendersonkleber.product.exception.ResourceAlreadyExistsException;
import com.hendersonkleber.product.exception.ResourceNotFoundException;
import com.hendersonkleber.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
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

        this.logger.info("Finding products page: {}, limit: {}, sort: {}, order: {}", page, limit, sort, order);

        var pageResponse = this.productRepository.findAll(pageRequest);

        return new PaginatedResponse<>(
                pageResponse.getContent().stream().map(ProductResponse::fromEntity).toList(),
                pageResponse.getTotalPages(),
                pageResponse.getTotalElements()
        );
    }

    public ProductResponse getById(Long id) {
        this.logger.info("Finding product by id: {}", id);

        var entity = this.productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return ProductResponse.fromEntity(entity);
    }

    public ProductResponse create(ProductRequest request) {
        this.logger.info("Creating product with name: {} and price: {}", request.name(), request.price());

        if (this.productRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Product with this name already exists");
        }

        var entity = this.productRepository.saveAndFlush(ProductRequest.toEntity(request));

        return ProductResponse.fromEntity(entity);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        this.logger.info("Updating product with id: {}, name: {} and price: {}", request.id(), request.name(), request.price());

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
        this.logger.info("Deleting product by id: {}", id);

        if (!this.productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        this.productRepository.deleteById(id);
    }
}
