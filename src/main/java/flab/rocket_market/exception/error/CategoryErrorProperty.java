package flab.rocket_market.exception.error;

import flab.rocket_market.global.exception.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static flab.rocket_market.global.message.MessageConstants.CATEGORY_NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorProperty implements ErrorProperty {

    CATEGORY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, CATEGORY_NOT_FOUND.getMessage());

    private final HttpStatus status;
    private final String message;
}
