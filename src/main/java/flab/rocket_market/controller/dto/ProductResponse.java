package flab.rocket_market.controller.dto;

import flab.rocket_market.entity.Products;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponse {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponse of(Products product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryName(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
