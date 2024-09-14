package flab.rocket_market.users.exception;

import flab.rocket_market.users.exception.error.UserErrorProperty;
import flab.rocket_market.global.exception.RocketMarketException;

public class UserNotFoundException extends RocketMarketException {

    public static final UserNotFoundException EXCEPTION = new UserNotFoundException();

    private UserNotFoundException() {
        super(UserErrorProperty.USER_NOT_FOUND);
    }
}
