package com.ys.authentication.adapter.infrastructure.persistence;

import com.ys.user.domain.User;
import com.ys.user.domain.UserId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<User, UserId> {
    Mono<User> findByIdAndWithdrawnAtIsNull(UserId userId);
    Mono<User> findByEmailAndWithdrawnAtIsNull(String email);
}
