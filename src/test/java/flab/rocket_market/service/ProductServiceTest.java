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

    private static final Long DEFAULT_CATEGORY_ID = 1L;
    private static final String DEFAULT_CATEGORY_NAME = "의류";
    private static final String DEFAULT_CATEGORY_DESCRIPTION = "패션 의류";
    private static final String DEFAULT_PRODUCT_NAME = "티셔츠";
    private static final String DEFAULT_PRODUCT_DESCRIPTION = "티셔츠 입니다.";
    private static final BigDecimal DEFAULT_PRODUCT_PRICE = BigDecimal.valueOf(5000);

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Categories defaultCategory;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        defaultCategory = createCategory(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_NAME, DEFAULT_CATEGORY_DESCRIPTION);
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("단건 상품 조회")
    void getProductById() {
        //given
        Long productId = 1L;
        Products product = createProduct(productId, defaultCategory);

        //when
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse result = productService.getProductById(productId);

        //then
        assertProductResponse(result, productId, DEFAULT_PRODUCT_NAME, DEFAULT_CATEGORY_NAME);
    }

    @Test
    @DisplayName("단건 품목 조회 - 품목이 없는 경우 에러")
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
        RegisterProductRequest request = createRegisterProductRequest(DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_DESCRIPTION, DEFAULT_PRODUCT_PRICE, DEFAULT_CATEGORY_ID);
        Long productId = 1L;
        Products savedProduct = createProduct(productId, defaultCategory);

        //when
        when(categoryRepository.findById(DEFAULT_CATEGORY_ID)).thenReturn(Optional.of(defaultCategory));
        when(productRepository.save(any(Products.class))).thenReturn(savedProduct);

        ProductResponse result = productService.registerProduct(request);

        //then
        assertProductResponse(result, productId, DEFAULT_PRODUCT_NAME, DEFAULT_CATEGORY_NAME);
    }

    @Test
    @DisplayName("품목 저장 - 카테고리가 없는 경우 에러")
    void registerProduct_NotFoundCategory() {
        //given
        RegisterProductRequest request = createRegisterProductRequest(DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_DESCRIPTION, DEFAULT_PRODUCT_PRICE, 2L);

        //when
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        //then
        assertThrows(CategoryNotFoundException.class, () -> productService.registerProduct(request));
    }

    @Test
    @DisplayName("품목 수정")
    void updateProduct() {
        //given
        Long productId = 1L;
        Products product = createProduct(productId, defaultCategory);

        Categories newCategory = createCategory(2L, "식품", "식품");
        UpdateProductRequest request = createUpdateProductRequest(productId, "오렌지", "오렌지 입니다.", BigDecimal.valueOf(3000), 2L);

        //when
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.updateProduct(request);

        //then
        assertProductResponse(ProductResponse.of(product), productId, product.getName(), product.getCategory().getName());
    }

    @Test
    @DisplayName("품목 수정 - 품목이 없는 경우 에러")
    void updateProduct_NotFound() {
        //given
        Long productId = 2L;

        UpdateProductRequest request = createUpdateProductRequest(productId, "오렌지", "오렌지 입니다.", BigDecimal.valueOf(3000), 2L);

        //when
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(request));
    }

    @Test
    @DisplayName("품목 수정 - 카테고리가 없는 경우 에러")
    void updateProduct_NotFoundCategory() {
        //given
        Long productId = 1L;
        Products product = createProduct(productId, defaultCategory);

        UpdateProductRequest request = createUpdateProductRequest(productId, "오렌지", "오렌지 입니다.", BigDecimal.valueOf(3000), 2L);

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
        Products product = createProduct(productId, defaultCategory);

        //when
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        //then
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("품목 삭제 - 품목이 없는 경우 에러")
    void deleteProduct_NotFound() {
        //given
        Long productId = 2L;

        //when
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId));
    }

    private Categories createCategory(Long categoryId, String name, String description) {
        return Categories.builder()
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .build();
    }

    private Products createProduct(Long productId, Categories categories) {
        return Products.builder()
                .productId(productId)
                .name(DEFAULT_PRODUCT_NAME)
                .description(DEFAULT_PRODUCT_DESCRIPTION)
                .price(DEFAULT_PRODUCT_PRICE)
                .category(categories)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private RegisterProductRequest createRegisterProductRequest(String name, String description, BigDecimal price, Long categoryId) {
        return RegisterProductRequest.builder()
                .name(name)
                .description(description)
                .price(price)
                .categoryId(categoryId)
                .build();
    }

    private UpdateProductRequest createUpdateProductRequest(Long productId, String name, String description, BigDecimal price, Long categoryId) {
        return UpdateProductRequest.builder()
                .productId(productId)
                .name(name)
                .description(description)
                .price(price)
                .categoryId(categoryId)
                .build();
    }

    private void assertProductResponse(ProductResponse response, Long expectedProductId, String expectedName, String expectedCategoryName) {
        assertThat(response.getProductId()).isEqualTo(expectedProductId);
        assertThat(response.getName()).isEqualTo(expectedName);
        assertThat(response.getCategoryName()).isEqualTo(expectedCategoryName);
    }
}