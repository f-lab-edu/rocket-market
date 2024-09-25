package flab.rocket_market.products.exception.error;

import flab.rocket_market.global.exception.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static flab.rocket_market.global.message.MessageConstants.PRODUCT_NOT_FOUND;
import static flab.rocket_market.global.message.MessageConstants.PRODUCT_SEARCH_FAILED;

@Getter
@RequiredArgsConstructor
public enum ProductErrorProperty implements ErrorProperty {

    PRODUCT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND.getMessage()),
    PRODUCT_SEARCH_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, PRODUCT_SEARCH_FAILED.getMessage());

    private final HttpStatus status;
    private final String message;
}
