package com.hendersonkleber.product.controller;

import com.hendersonkleber.product.dto.PaginatedResponse;
import com.hendersonkleber.product.dto.ProductRequest;
import com.hendersonkleber.product.dto.ProductResponse;
import com.hendersonkleber.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(path = "/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<ProductResponse>> getAll(
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "Page must be greater than or equal to zero")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 10, message = "Limit must be greater than or equal to 10")
            @Max(value = 50, message = "Limit must be less than or equal to 50")
            int limit,

            @RequestParam(defaultValue = "id")
            @Pattern(regexp = "id|name|price", message = "Sort must be one of: id, name or price")
            String sort,

            @RequestParam(defaultValue = "desc")
            @Pattern(regexp = "asc|desc", message = "Order must be either asc or desc")
            String order
    ) {
        var response = this.productService.getAll(page, limit, sort, order);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ProductResponse> getById(
            @PathVariable
            @Positive(message = "Id must be a positive number")
            Long id
    ) {
        var response = this.productService.getById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @RequestBody
            @Valid
            ProductRequest body
    ) {
        var response = this.productService.create(body);
        return ResponseEntity.created(URI.create("/products/" + response.id())).body(response);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable
            @Positive(message = "Id must be a positive number")
            Long id,

            @RequestBody
            @Valid
            ProductRequest body
    ) {
        var response = this.productService.update(id, body);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable
            @Positive(message = "Id must be a positive number")
            Long id
    ) {
        this.productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
