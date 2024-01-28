package com.ys.user.adapter.out.persistence;

import com.ys.user.domain.UserEntity;
import com.ys.user.domain.UserId;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface UserEntityRepository {
    void insert(UserEntity userEntity);
    void updateByPasswordWrongCount(UserEntity userEntity);
    void updateByLastLoginAtAndPasswordWrongCount(UserEntity userEntity);
    void updateByPassword(UserEntity userEntity);
    void updateByProfile(UserEntity userEntity);
    void updateByWithdrawnAt(UserEntity userEntity);

    Optional<UserEntity> selectOneById(UserId userId);
    Optional<UserEntity> selectOneByIdAndWithdrawnAtIsNull(UserId userId);
    Optional<UserEntity> selectOneByEmail(String email);
}
