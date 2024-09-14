package flab.rocket_market.orders.dto;

import flab.rocket_market.orders.entity.OrderItems;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;

    public static OrderItemResponse of(OrderItems orderItems) {
        return OrderItemResponse.builder()
                .productId(orderItems.getProducts().getProductId())
                .name(orderItems.getProducts().getName())
                .quantity(orderItems.getQuantity())
                .price(orderItems.getPrice())
                .build();
    }
}
