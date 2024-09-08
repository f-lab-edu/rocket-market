package flab.rocket_market.service;

import flab.rocket_market.dto.PageResponse;
import flab.rocket_market.dto.ProductResponse;
import flab.rocket_market.dto.RegisterProductRequest;
import flab.rocket_market.dto.UpdateProductRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private static final int PAGE = 0;
    private static final int SIZE = 10;

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
    void getProductByIdNotFound() {
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
    void registerProductCategoryNotFound() {
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
    void updateProductNotFound() {
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
    void updateProductCategoryNotFound() {
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
    void deleteProductNotFound() {
        //given
        Long productId = 2L;

        //when
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId));
    }

    @Test
    @DisplayName("품목 전체 조회")
    void getProducts() {
        //given
        Pageable pageable = PageRequest.of(PAGE, SIZE);
        Page<Products> productsPage = createProductsPage(pageable, 1L, 2L);

        when(productRepository.findAll(pageable)).thenReturn(productsPage);

        //when
        PageResponse<ProductResponse> result = productService.getProducts(PAGE, SIZE);

        //then
        assertEquals(result.getTotalElement(), 2);
        assertProductResponse(result.getContent().get(0), 1L, DEFAULT_PRODUCT_NAME, defaultCategory.getName());
    }

    @Test
    @DisplayName("품목 검색")
    void searchProducts() {
        //given
        String keyword = DEFAULT_PRODUCT_NAME;
        Pageable pageable = PageRequest.of(PAGE, SIZE);
        Page<Products> productsPage = createProductsPage(pageable, 1L, 2L);

        when(productRepository.searchByKeyword(keyword + "*", pageable)).thenReturn(productsPage);

        //when
        PageResponse<ProductResponse> result = productService.searchProducts(keyword, PAGE, SIZE);

        //then
        assertEquals(result.getTotalElement(), 2);
        assertProductResponse(result.getContent().get(0), 1L, DEFAULT_PRODUCT_NAME, defaultCategory.getName());
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

    private Page<Products> createProductsPage(Pageable pageable, Long ...productId) {
        List<Products> list = new ArrayList<>();

        for (Long id : productId) {
            Products product = createProduct(id, defaultCategory);
            list.add(product);
        }

        Page<Products> productsPage = new PageImpl<>(list, pageable, list.size());
        return productsPage;
    }

    private void assertProductResponse(ProductResponse response, Long expectedProductId, String expectedName, String expectedCategoryName) {
        assertThat(response.getProductId()).isEqualTo(expectedProductId);
        assertThat(response.getName()).isEqualTo(expectedName);
        assertThat(response.getCategoryName()).isEqualTo(expectedCategoryName);
    }
}