package com.ys.authentication.adapter.out.cache;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthenticationInfoRedisRepository extends CrudRepository<AuthenticationInfoRedisEntity, Long> {
    List<AuthenticationInfoRedisEntity> findAllByUserId(Long userId);
}
