package com.ys.user.domain.event;

import com.ys.user.adapter.in.model.UserModel;
import com.ys.user.domain.Account;
import com.ys.user.domain.Profile;
import com.ys.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserEvent {
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

    public static UserEvent fromDomain(User user) {
        Account account = user.getAccount();
        Profile profile = user.getProfile();
        return new UserEvent(
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
