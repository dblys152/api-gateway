package com.ys.authentication.adapter.infrastructure.persistence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, Long> {
    @Query("SELECT  * " +
            "FROM   USERS " +
            "WHERE  USER_ID = :userId " +
            "AND    WITHDRAWN_AT IS NULL")
    Mono<UserEntity> findByIdAndWithdrawnAtIsNull(Long userId);

    @Query("SELECT  * " +
            "FROM   USERS " +
            "WHERE  EMAIL = :email " +
            "AND    WITHDRAWN_AT IS NULL")
    Mono<UserEntity> findByEmailAndWithdrawnAtIsNull(String email);
}
