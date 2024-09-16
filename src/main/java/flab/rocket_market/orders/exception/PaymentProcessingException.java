package flab.rocket_market.orders.exception;

import flab.rocket_market.orders.exception.error.OrderErrorProperty;
import flab.rocket_market.global.exception.RocketMarketException;

public class PaymentProcessingException extends RocketMarketException {

    public static final PaymentProcessingException EXCEPTION = new PaymentProcessingException();

    private PaymentProcessingException() {
        super(OrderErrorProperty.PAYMENT_PROCESSING_EXCEPTION);
    }
}
