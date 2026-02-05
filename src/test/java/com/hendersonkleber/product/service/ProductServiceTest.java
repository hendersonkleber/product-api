package com.hendersonkleber.product.service;

import com.hendersonkleber.product.domain.Product;
import com.hendersonkleber.product.dto.ProductRequest;
import com.hendersonkleber.product.exception.ResourceAlreadyExistsException;
import com.hendersonkleber.product.exception.ResourceNotFoundException;
import com.hendersonkleber.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Captor
    private ArgumentCaptor<Product> captor;

    @InjectMocks
    private ProductService productService;

    @Nested
    public class GetAll {
        @Test
        @DisplayName("Should return products paginated")
        void shouldReturnProductsPaginated() {
            // arrange
            int page = 0;
            int limit = 10;
            String sort = "id";
            String order = "desc";
            var product = new Product(1L, "Henderson", BigDecimal.valueOf(10));
            List<Product> content = List.of(product);
            Pageable pageRequest = PageRequest.of(page, limit, order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sort);
            Page<Product> pageResponse = new PageImpl<>(content, pageRequest, content.size());

            doReturn(pageResponse).when(productRepository).findAll(any(Pageable.class));

            // act
            var response = productService.getAll(page, limit, sort, order);

            // assert
            verify(productRepository, times(1)).findAll(any(Pageable.class));

            assertNotNull(response);
            assertEquals(content.size(), response.content().size());
            assertEquals(1, response.totalItems());
            assertEquals(1, response.totalPages());
        }

        @Test
        @DisplayName("Should return empty content when does not exists products")
        void shouldReturnEmptyContentWhenDoesNotExistsProducts() {
            // arrange
            int page = 0;
            int limit = 10;
            String sort = "id";
            String order = "desc";

            List<Product> content = List.of();
            Pageable pageRequest = PageRequest.of(page, limit, order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sort);
            Page<Product> pageResponse = new PageImpl<>(content, pageRequest, content.size());

            doReturn(pageResponse).when(productRepository).findAll(any(Pageable.class));

            // act
            var response = productService.getAll(page, limit, sort, order);

            // assert
            verify(productRepository, times(1)).findAll(any(Pageable.class));

            assertNotNull(response);
            assertEquals(0, response.content().size());
            assertEquals(0, response.totalItems());
            assertEquals(0, response.totalPages());
        }
    }

    @Nested
    public class GetById {
        @Test
        @DisplayName("Should get by id successfully")
        void shouldGetByIdSuccessfully() {
            // arrange
            Long id = 1L;
            var product = new Product(id, "Henderson", BigDecimal.valueOf(10));
            doReturn(Optional.of(product)).when(productRepository).findById(id);

            // act
            var response = productService.getById(id);

            // assert
            verify(productRepository, times(1)).findById(id);

            assertNotNull(response);
            assertEquals(product.getName(), response.name());
            assertEquals(product.getPrice(), response.price());
        }

        @Test
        @DisplayName("Should throw exception when product does not exist")
        void shouldThrowExceptionWhenProductDoesNotExist() {
            // arrange
            Long id = 1L;
            doReturn(Optional.empty()).when(productRepository).findById(eq(id));

            // assert & act
            assertThrows(ResourceNotFoundException.class, () -> productService.getById(id));
            verify(productRepository, times(1)).findById(id);
        }
    }

    @Nested
    public class Create {
        @Test
        @DisplayName("Should create")
        void shouldCreate() {
            // arrange
            var request = new ProductRequest(0L, "Henderson", BigDecimal.valueOf(10));

            doReturn(false).when(productRepository).existsByName(request.name());

            // act
            productService.create(request);

            // assert
            verify(productRepository, times(1)).existsByName(request.name());
            verify(productRepository, times(1)).saveAndFlush(captor.capture());

            var response = captor.getValue();

            assertNotNull(response);
            assertEquals(request.name(), response.getName());
            assertEquals(request.price(), response.getPrice());
        }

        @Test
        @DisplayName("Should throw exception when product name already exists")
        void shouldThrowExceptionWhenProductNameAlreadyExists() {
            // arrange
            var request = new ProductRequest(0L, "Henderson", BigDecimal.valueOf(10));

            doReturn(true).when(productRepository).existsByName(request.name());

            // assert & act
            assertThrows(ResourceAlreadyExistsException.class, () -> productService.create(request));

            verify(productRepository, times(1)).existsByName(request.name());
            verify(productRepository, times(0)).save(any());
        }
    }

    @Nested
    public class Update {
        @Test
        @DisplayName("Should update")
        void shouldUpdate() {
            // arrange
            Long id = 1L;

            var request = new ProductRequest(id, "Henderson", BigDecimal.valueOf(10));
            var product = new Product(id, "Henderson", BigDecimal.valueOf(10));

            doReturn(false).when(productRepository).existsByName(request.name(), id);
            doReturn(Optional.of(product)).when(productRepository).findById(id);

            // act
            productService.update(id, request);

            // assert
            verify(productRepository).existsByName(request.name(), id);
            verify(productRepository).findById(id);
            verify(productRepository).saveAndFlush(captor.capture());

            var response = captor.getValue();

            assertNotNull(response);
            assertEquals(request.id(), response.getId());
            assertEquals(request.name(), response.getName());
            assertEquals(request.price(), response.getPrice());
        }

        @Test
        @DisplayName("Should throw exception when product name already exists")
        void shouldThrowExceptionWhenProductNameAlreadyExists() {
            // arrange
            Long id = 1L;
            var request = new ProductRequest(id, "Henderson", BigDecimal.valueOf(10));

            doReturn(true).when(productRepository).existsByName(request.name(), request.id());

            // assert & act
            assertThrows(ResourceAlreadyExistsException.class, () -> productService.update(id, request));

            verify(productRepository, times(1)).existsByName(request.name(), request.id());
            verify(productRepository, times(0)).findById(any());
            verify(productRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("Should throw exception when product does not exist")
        void shouldThrowExceptionWhenProductDoesNotExist() {
            // arrange
            Long id = 1L;
            var request = new ProductRequest(id, "Henderson", BigDecimal.valueOf(10));

            doReturn(false).when(productRepository).existsByName(request.name(), request.id());
            doReturn(Optional.empty()).when(productRepository).findById(id);

            // assert & act
            assertThrows(ResourceNotFoundException.class, () -> productService.update(id, request));

            verify(productRepository, times(1)).findById(id);
            verify(productRepository, times(0)).save(any());
        }
    }

    @Nested
    public class Delete {
        @Test
        @DisplayName("Should delete")
        void shouldDelete() {
            // arrange
            Long id = 1L;
            doReturn(true).when(productRepository).existsById(eq(id));

            // act
            productService.delete(id);

            // assert
            verify(productRepository, times(1)).existsById(id);
            verify(productRepository, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("Should throw exception when product does not exist")
        void shouldThrowExceptionWhenProductDoesNotExist() {
            // arrange
            Long id = 1L;
            doReturn(false).when(productRepository).existsById(eq(id));

            // assert & act
            assertThrows(ResourceNotFoundException.class, () -> productService.delete(id));

            verify(productRepository, times(1)).existsById(id);
        }
    }
}