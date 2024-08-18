package flab.rocket_market.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class UpdateProductRequest {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
}
