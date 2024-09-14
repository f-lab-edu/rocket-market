package flab.rocket_market.orders.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequest {

    @NotNull(message = "{product.id.notnull}")
    private Long productId;

    @Min(value = 1, message = "{order.quantity.min}")
    @Max(value = 1000, message = "{order.quantity.max}")
    private Integer quantity;
}
