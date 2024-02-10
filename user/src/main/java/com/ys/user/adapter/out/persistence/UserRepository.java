package com.ys.user.adapter.out.persistence;

import com.ys.user.domain.User;
import com.ys.user.domain.UserId;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserRepository {
    void insert(User user);
    void updateByPasswordWrongCount(User user);
    void updateByLastLoginAtAndPasswordWrongCount(User user);
    void updateByPassword(User user);
    void updateByProfile(User user);
    void updateByWithdrawnAt(User user);

    Optional<User> selectOneByIdAndWithdrawnAtIsNull(UserId userId);
    List<User> selectAllByEmailAndWithdrawnAtIsNull(String email);
    List<User> selectAllByMobileAndWithdrawnAtIsNull(String mobile);
}
