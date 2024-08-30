package flab.rocket_market.aop;

import flab.rocket_market.dto.User;
import flab.rocket_market.exception.AccessDeniedException;
import flab.rocket_market.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final UserService userService;

    @Before("@annotation(flab.rocket_market.aop.RequiredRole) && @annotation(requiredRole)")
    public void checkUserRole(RequiredRole requiredRole) {
        User user = userService.getUser();

        if (!user.getRole().equals(requiredRole.value())) {
            throw AccessDeniedException.EXCEPTION;
        }
    }
}
