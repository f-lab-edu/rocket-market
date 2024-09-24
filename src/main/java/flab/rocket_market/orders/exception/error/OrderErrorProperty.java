package flab.rocket_market.orders.exception.error;

import flab.rocket_market.global.exception.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorProperty implements ErrorProperty {

    OUT_OF_STOCK_EXCEPTION(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    PAYMENT_PROCESSING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "결제 API 호출에 실패하였습니다."),
    PAYMENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "결제 정보가 없습니다."),
    ORDER_FAILED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "주문 데이터 저장에 실패하였습니다.");

    private final HttpStatus status;
    private final String message;
}
