package flab.rocket_market.orders.exception;

import flab.rocket_market.orders.exception.error.OrderErrorProperty;
import flab.rocket_market.global.exception.RocketMarketException;

public class OutOfStockException extends RocketMarketException {

    public static final OutOfStockException EXCEPTION = new OutOfStockException();

    private OutOfStockException() {
        super(OrderErrorProperty.OUT_OF_STOCK_EXCEPTION);
    }
}
