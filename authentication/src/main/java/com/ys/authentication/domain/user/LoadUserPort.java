package com.ys.authentication.domain.user;

import com.ys.user.domain.User;
import com.ys.user.domain.UserId;
import reactor.core.publisher.Mono;

public interface LoadUserPort {
    Mono<User> findByIdAndWithdrawnAtIsNull(UserId userId);
    Mono<User> findByEmailAndWithdrawnAtIsNull(String email);
}
