package flab.rocket_market.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseDataResponse<T> extends BaseResponse {

    private final T data;

    public BaseDataResponse(Integer status, String message, T data) {
        super(status, message);
        this.data = data;
    }

    public static <T> BaseDataResponse<T> of(HttpStatus status, String message, T data) {
        return new BaseDataResponse<>(status.value(), message, data);
    }
}
