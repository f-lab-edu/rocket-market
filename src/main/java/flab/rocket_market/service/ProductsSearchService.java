package flab.rocket_market.service;

import flab.rocket_market.dto.PageResponse;
import flab.rocket_market.dto.ProductResponse;
import flab.rocket_market.entity.Products;
import flab.rocket_market.document.ProductsDocument;
import flab.rocket_market.repository.ProductsSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductsSearchService {

    private final ProductsSearchRepository productsSearchRepository;

    public PageResponse<ProductResponse> searchProductsFromElasticsearch(String keyword, Pageable pageable) {
        Page<ProductsDocument> productsDocumentPage = productsSearchRepository.findByNameContaining(keyword, pageable);

        return getPageResponse(productsDocumentPage);
    }

    public void saveProductsToElasticsearch(Products products) {
        ProductsDocument productsDocument = ProductsDocument.builder()
                .productsId(products.getProductId())
                .name(products.getName())
                .description(products.getDescription())
                .price(products.getPrice())
                .categoryName(products.getCategory().getName())
                .createdAt(products.getCreatedAt())
                .updatedAt(products.getUpdatedAt())
                .build();

        productsSearchRepository.save(productsDocument);
    }

    public void deleteProductsFromElasticsearch(Long productId) {
        productsSearchRepository.deleteById(productId);
    }

    private PageResponse<ProductResponse> getPageResponse(Page<ProductsDocument> productsPage) {
        List<ProductResponse> productResponses = productsPage.getContent().stream()
                .map(products -> ProductResponse.builder()
                        .productId(products.getProductsId())
                        .name(products.getName())
                        .description(products.getDescription())
                        .price(products.getPrice())
                        .categoryName(products.getCategoryName())
                        .createdAt(products.getCreatedAt())
                        .updatedAt(products.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return new PageResponse<>(
                productResponses,
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.isLast()
        );
    }
}
