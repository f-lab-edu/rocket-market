package flab.rocket_market.products.dto;

import jakarta.validation.constraints.NotBlank;
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
public class RegisterProductRequest {

    @NotBlank(message = "{product.name.notblank}")
    private String name;

    private String description;

    @NotNull(message = "{product.price.notnull}")
    private BigDecimal price;

    @NotNull(message = "{product.category.notnull}")
    private Long categoryId;
}
