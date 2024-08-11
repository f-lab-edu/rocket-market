package flab.rocket_market.exception.error;

import flab.rocket_market.global.exception.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorProperty implements ErrorProperty {

    PRODUCT_NOTFOUNTD_EXCEPTION(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
