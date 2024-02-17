package com.ys.user.adapter.in.model;

import com.ys.user.domain.Account;
import com.ys.user.domain.Profile;
import com.ys.user.domain.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserModel {
    Long userId;
    String email;
    LocalDateTime passwordModifiedAt;
    int passwordWrongCount;
    LocalDateTime lastLoginAt;
    String name;
    String mobile;
    String birthDate;
    String gender;
    String roles;
    LocalDateTime joinedAt;
    LocalDateTime modifiedAt;
    LocalDateTime withdrawnAt;

    public static UserModel fromDomain(User user) {
        Account account = user.getAccount();
        Profile profile = user.getProfile();
        return new UserModel(
                user.getUserId().get(),
                account.getEmail(),
                account.getPasswordModifiedAt(),
                account.getPasswordWrongCount(),
                account.getLastLoginAt(),
                profile.getName(),
                profile.getMobile(),
                profile.getBirthDate(),
                profile.getGender(),
                user.getRoles(),
                user.getJoinedAt(),
                user.getModifiedAt(),
                user.getWithdrawnAt()
        );
    }
}
