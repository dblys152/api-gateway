package com.ys.authentication.adapter.out.persistence;

import com.ys.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserRepository {
    void updateByPasswordWrongCount(User user);
    void updateByLastLoginAtAndPasswordWrongCount(User user);
    Optional<User> selectOneByEmailAndWithdrawnAtIsNull(String email);
}
