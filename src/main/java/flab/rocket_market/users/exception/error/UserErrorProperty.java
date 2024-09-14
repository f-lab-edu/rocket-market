package flab.rocket_market.users.exception.error;

import flab.rocket_market.global.exception.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorProperty implements ErrorProperty {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}