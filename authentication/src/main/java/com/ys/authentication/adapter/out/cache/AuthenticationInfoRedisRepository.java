package com.ys.authentication.adapter.out.cache;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthenticationInfoRedisRepository extends CrudRepository<AuthenticationInfoRedisEntity, Long> {
    Optional<AuthenticationInfoRedisEntity> findByRefreshToken(String refreshToken);
    List<AuthenticationInfoRedisEntity> findAllByUserId(Long userId);
}
