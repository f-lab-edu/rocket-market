package flab.rocket_market.products.service;

import flab.rocket_market.products.document.ProductsDocument;
import flab.rocket_market.products.dto.PageResponse;
import flab.rocket_market.products.dto.ProductResponse;
import flab.rocket_market.products.entity.Categories;
import flab.rocket_market.products.repository.ProductsSearchRepository;
import flab.rocket_market.products.service.ProductsSearchService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductsSearchServiceTest {

    private static final Long DEFAULT_CATEGORY_ID = 1L;
    private static final String DEFAULT_CATEGORY_NAME = "의류";
    private static final String DEFAULT_CATEGORY_DESCRIPTION = "패션 의류";
    private static final String DEFAULT_PRODUCT_NAME = "티셔츠";
    private static final String DEFAULT_PRODUCT_DESCRIPTION = "티셔츠 입니다.";
    private static final BigDecimal DEFAULT_PRODUCT_PRICE = BigDecimal.valueOf(5000);

    private static final int PAGE = 0;
    private static final int SIZE = 10;

    @Mock
    private ProductsSearchRepository productsSearchRepository;

    @InjectMocks
    private ProductsSearchService productsSearchService;

    private Categories defaultCategory;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        defaultCategory = createCategory(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_NAME, DEFAULT_CATEGORY_DESCRIPTION);
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("품목 검색 - 엘라스틱서치")
    void searchProductsFromElasticsearch() {
        //given
        String keyword = DEFAULT_PRODUCT_NAME;
        Pageable pageable = PageRequest.of(PAGE, SIZE);
        Page<ProductsDocument> productsDocumentPage = createProductsDocumentPage(pageable, 1L, 2L);

        when(productsSearchRepository.findByNameContaining(keyword, pageable)).thenReturn(productsDocumentPage);

        //when
        PageResponse<ProductResponse> result = productsSearchService.searchProductsFromElasticsearch(keyword, PAGE, SIZE);

        //then
        assertEquals(result.getTotalElement(), 2);
        assertProductResponse(result.getContent().get(0), 1L, DEFAULT_PRODUCT_NAME, defaultCategory.getName());

    }

    @Test
    @DisplayName("품목 검색 - 검색 결과 없음")
    void searchProductsFromElasticsearchNoResults() {
        //given
        String keyword = DEFAULT_PRODUCT_NAME;
        Pageable pageable = PageRequest.of(PAGE, SIZE);
        Page<ProductsDocument> emptyPage = Page.empty(pageable);

        when(productsSearchRepository.findByNameContaining(keyword, pageable)).thenReturn(emptyPage);

        //when
        PageResponse<ProductResponse> result = productsSearchService.searchProductsFromElasticsearch(keyword, PAGE, SIZE);

        //then
        assertTrue(result.getContent().isEmpty());
    }

    private Categories createCategory(Long categoryId, String name, String description) {
        return Categories.builder()
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .build();
    }

    private ProductsDocument createProductDocument(Long productId, Categories categories) {
        return ProductsDocument.builder()
                .productsId(productId)
                .name(DEFAULT_PRODUCT_NAME)
                .description(DEFAULT_PRODUCT_DESCRIPTION)
                .price(DEFAULT_PRODUCT_PRICE)
                .categoryName(categories.getName())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private Page<ProductsDocument> createProductsDocumentPage(Pageable pageable, Long ...productId) {
        List<ProductsDocument> list = new ArrayList<>();

        for (Long id : productId) {
            ProductsDocument product = createProductDocument(id, defaultCategory);
            list.add(product);
        }

        Page<ProductsDocument> productsPage = new PageImpl<>(list, pageable, list.size());
        return productsPage;
    }

    private void assertProductResponse(ProductResponse response, Long expectedProductId, String expectedName, String expectedCategoryName) {
        assertThat(response.getProductId()).isEqualTo(expectedProductId);
        assertThat(response.getName()).isEqualTo(expectedName);
        assertThat(response.getCategoryName()).isEqualTo(expectedCategoryName);
    }
}