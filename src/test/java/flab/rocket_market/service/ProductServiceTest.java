package flab.rocket_market.service;

import flab.rocket_market.controller.dto.ProductResponse;
import flab.rocket_market.controller.dto.RegisterProductRequest;
import flab.rocket_market.controller.dto.UpdateProductRequest;
import flab.rocket_market.entity.Categories;
import flab.rocket_market.entity.Products;
import flab.rocket_market.exception.CategoryNotFoundException;
import flab.rocket_market.exception.ProductNotFoundException;
import flab.rocket_market.repository.CategoryRepository;
import flab.rocket_market.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Categories categories;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        categories = Categories.builder()
                .categoryId(1L)
                .name("의류")
                .description("패션 의류")
                .build();

        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("단건 상품 조회")
    void getProductById() {
        //given
        Long productId = 1L;
        Products product = createProduct(productId);

        //when
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse result = productService.getProductById(productId);

        //then
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("티셔츠");
    }

    @Test
    @DisplayName("조회 품목이 없을 경우")
    void getProductById_NotFound() {
        //given
        Long productId = 2L;

        //when
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
    }

    @Test
    @DisplayName("품목 저장")
    void registerProduct() {
        //given
        RegisterProductRequest request = RegisterProductRequest.builder()
                .name("티셔츠")
                .description("티셔츠 입니다.")
                .price(BigDecimal.valueOf(5000))
                .categoryId(1L)
                .build();

        Long productId = 1L;
        Products savedProduct = createProduct(productId);

        //when
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categories));
        when(productRepository.save(any(Products.class))).thenReturn(savedProduct);

        ProductResponse result = productService.registerProduct(request);

        //then
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("티셔츠");
        assertThat(result.getCategoryName()).isEqualTo("의류");
    }

    @Test
    @DisplayName("품목 저장 시 카테고리가 없을 경우")
    void registerProduct_NotFoundCategory() {
        //given
        RegisterProductRequest request = RegisterProductRequest.builder()
                .name("티셔츠")
                .description("티셔츠 입니다.")
                .price(BigDecimal.valueOf(5000))
                .categoryId(2L)
                .build();

        //when
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        //then
        assertThrows(CategoryNotFoundException.class, () -> productService.registerProduct(request));
    }

    @Test
    @DisplayName("품목 업데이트")
    void updateProduct() {
        //given
        Long productId = 1L;
        Products product = createProduct(productId);

        Categories newCategory = Categories.builder()
                .name("식품")
                .description("식품")
                .categoryId(2L)
                .build();

        UpdateProductRequest request = UpdateProductRequest.builder()
                .productId(productId)
                .name("오렌지")
                .description("오렌지 입니다.")
                .categoryId(2L)
                .price(BigDecimal.valueOf(3000))
                .build();

        //when
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.updateProduct(request);

        //then
        assertThat(product.getProductId()).isEqualTo(productId);
        assertThat(product.getName()).isEqualTo("오렌지");
        assertThat(product.getCategory()).isEqualTo(newCategory);
    }

    @Test
    @DisplayName("품목 업데이트 시 카테고리가 없는 경우")
    void updateProduct_NotFoundCategory() {
        //given
        Long productId = 1L;
        Products product = createProduct(productId);

        UpdateProductRequest request = UpdateProductRequest.builder()
                .productId(productId)
                .name("오렌지")
                .description("오렌지 입니다.")
                .categoryId(2L)
                .price(BigDecimal.valueOf(3000))
                .build();

        //when
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        //then
        assertThrows(CategoryNotFoundException.class, () -> productService.updateProduct(request));
    }

    @Test
    @DisplayName("품목 삭제")
    void deleteProduct() {
        //given
        Long productId = 1L;
        Products product = createProduct(productId);

        //when
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        //then
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("삭제할 품목이 없는 경우")
    void deleteProduct_NotFound() {
        //given
        Long productId = 2L;

        //when
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId));
    }

    private Products createProduct(Long productId) {
        return Products.builder()
                .productId(productId)
                .name("티셔츠")
                .description("티셔츠 입니다")
                .price(BigDecimal.valueOf(5000))
                .category(categories)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}