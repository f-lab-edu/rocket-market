package flab.rocket_market.exception;

import flab.rocket_market.exception.error.SecurityErrorProperty;
import flab.rocket_market.global.exception.RocketMarketException;
import flab.rocket_market.global.exception.error.ErrorProperty;

public class AccessDeniedException extends RocketMarketException {

    public static final AccessDeniedException EXCEPTION = new AccessDeniedException();

    private AccessDeniedException() {
        super(SecurityErrorProperty.ACCESS_DENIED_EXCEPTION);
    }
}
