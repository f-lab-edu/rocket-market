package flab.rocket_market.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class BaseResponse {

    private final Integer status;
    private final String message;

    public static BaseResponse of(HttpStatus status, String message) {
        return new BaseResponse(status.value(), message);
    }
}
