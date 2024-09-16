package flab.rocket_market.products.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {

    @NotNull(message = "{product.id.notnull}")
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
}
