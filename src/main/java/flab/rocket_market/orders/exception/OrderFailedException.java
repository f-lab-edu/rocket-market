package flab.rocket_market.orders.exception;

import flab.rocket_market.global.exception.RocketMarketException;
import flab.rocket_market.orders.exception.error.OrderErrorProperty;

public class OrderFailedException extends RocketMarketException {

    public static final OrderFailedException EXCEPTION = new OrderFailedException();

    public OrderFailedException() {
        super(OrderErrorProperty.ORDER_FAILED_EXCEPTION);
    }
}
