package flab.rocket_market.users.service;

import flab.rocket_market.users.dto.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public User getUser() {
        return new User("사용자 테스트", "ROLE_ADMIN");
    }
}
