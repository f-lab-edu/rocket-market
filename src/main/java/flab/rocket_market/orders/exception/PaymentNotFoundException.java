package flab.rocket_market.orders.exception;

import flab.rocket_market.global.exception.RocketMarketException;
import flab.rocket_market.orders.exception.error.OrderErrorProperty;

public class PaymentNotFoundException extends RocketMarketException {

    public static final PaymentNotFoundException EXCEPTION = new PaymentNotFoundException();

    public PaymentNotFoundException() {
        super(OrderErrorProperty.PAYMENT_NOT_FOUND_EXCEPTION);
    }
}
