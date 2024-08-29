package flab.rocket_market.global.message;

import lombok.Getter;

@Getter
public enum MessageConstants {

    PRODUCT_REGISTER_SUCCESS("성공적으로 등록되었습니다."),
    PRODUCT_RETRIEVE_SUCCESS("성공적으로 조회되었습니다."),
    PRODUCT_UPDATE_SUCCESS("성공적으로 수정되었습니다"),
    PRODUCT_DELETE_SUCCESS("성공적으로 삭제되었습니다."),

    CATEGORY_NOT_FOUND("카테고리가 존재하지 않습니다."),
    PRODUCT_NOT_FOUND("제품이 존재하지 않습니다."),

    VALIDATION_FAILED("유효성 검사에 실패하였습니다.");

    private final String message;

    MessageConstants(String message) {
        this.message = message;
    }
}
