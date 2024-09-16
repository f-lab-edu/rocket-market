package flab.rocket_market.orders.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PENDING("pending"),
    SHIPPED("shipped"),
    DELIVERED("delivered");

    private final String value;
}
