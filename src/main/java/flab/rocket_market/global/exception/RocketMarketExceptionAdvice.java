package flab.rocket_market.global.exception;

import flab.rocket_market.global.response.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RocketMarketExceptionAdvice {

    @ExceptionHandler(RocketMarketException.class)
    public BaseResponse handleException(RocketMarketException exception) {
        return BaseResponse.of(exception.getError().getStatus(), exception.getError().getMessage());
    }
}
