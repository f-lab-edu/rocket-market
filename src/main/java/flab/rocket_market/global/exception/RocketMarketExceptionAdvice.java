package flab.rocket_market.global.exception;

import flab.rocket_market.global.response.BaseDataResponse;
import flab.rocket_market.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static flab.rocket_market.global.message.MessageConstants.VALIDATION_FAILED;

@RestControllerAdvice
public class RocketMarketExceptionAdvice {

    @ExceptionHandler(RocketMarketException.class)
    public BaseResponse handleException(RocketMarketException exception) {
        return BaseResponse.of(exception.getError().getStatus(), exception.getError().getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseDataResponse<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return BaseDataResponse.of(HttpStatus.BAD_REQUEST, VALIDATION_FAILED.getMessage(), errors);
    }
}
