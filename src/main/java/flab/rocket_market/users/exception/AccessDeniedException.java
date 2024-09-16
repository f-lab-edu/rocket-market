package flab.rocket_market.users.exception;

import flab.rocket_market.users.exception.error.SecurityErrorProperty;
import flab.rocket_market.global.exception.RocketMarketException;

public class AccessDeniedException extends RocketMarketException {

    public static final AccessDeniedException EXCEPTION = new AccessDeniedException();

    private AccessDeniedException() {
        super(SecurityErrorProperty.ACCESS_DENIED_EXCEPTION);
    }
}
