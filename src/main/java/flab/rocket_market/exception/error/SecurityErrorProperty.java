package flab.rocket_market.exception.error;

import flab.rocket_market.global.exception.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static flab.rocket_market.global.message.MessageConstants.ACCESS_DENIED;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorProperty implements ErrorProperty {

    ACCESS_DENIED_EXCEPTION(HttpStatus.FORBIDDEN, ACCESS_DENIED.getMessage());

    private final HttpStatus status;
    private final String message;
}
