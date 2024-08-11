package flab.rocket_market.global.exception;

import flab.rocket_market.global.exception.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RocketMarketException extends RuntimeException {

    private final ErrorProperty error;
}
