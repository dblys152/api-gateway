package com.ys.user.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import com.ys.infrastructure.exception.UnauthorizedException;
import com.ys.infrastructure.jwt.PayloadInfo;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class User {
    @NotNull
    private UserId userId;

    @NotNull
    private UserType type;

    @NotNull
    private UserStatus status;

    @NotNull
    private Account account;

    @NotNull
    private Profile profile;

    @NotNull
    private String roles;

    @NotNull
    private LocalDateTime joinedAt;

    @NotNull
    private LocalDateTime modifiedAt;

    private LocalDateTime withdrawnAt;

    public static User of(
            UserId userId,
            UserType type,
            UserStatus status,
            Account account,
            Profile profile,
            String roles,
            LocalDateTime joinedAt,
            LocalDateTime modifiedAt,
            LocalDateTime withdrawnAt
    ) {
        return new User(userId, type, status, account, profile, roles, joinedAt, modifiedAt, withdrawnAt);
    }

    public static User create(CreateUserCommand command) {
        LocalDateTime now = LocalDateTime.now();
        return new User(
                UserId.of(TsidCreator.getTsid256().toLong()),
                UserType.USER,
                UserStatus.JOINED,
                command.getAccount(),
                command.getProfile(),
                UserRole.ROLE_USER.name(),
                now,
                now,
                null
        );
    }

    public boolean matchesPassword(String password) {
        return this.account.matchesPassword(password);
    }

    public void changePassword(String password) {
        this.account.changePassword(password);
        this.modifiedAt = LocalDateTime.now();
    }

    public void increasePasswordWrongCount() {
        this.account.increasePasswordWrongCount();
        this.modifiedAt = LocalDateTime.now();
    }

    public void validateExceededPasswordWrongCount() {
        this.account.validateExceededPasswordWrongCount();
    }

    public void initPasswordWrongCount() {
        this.account.initPasswordWrongCount();
        this.modifiedAt = LocalDateTime.now();
    }

    public void changeLastLoginAtAndInitPasswordWrongCount() {
        LocalDateTime now = LocalDateTime.now();
        this.account.modifyLastLoginAt(now);
        this.account.initPasswordWrongCount();
        this.modifiedAt = now;
    }

    public void changeProfile(ChangeUserProfileCommand command) {
        this.profile = command.getProfile();
        this.modifiedAt = LocalDateTime.now();
    }

    public void changeRoles(String roles) {
        this.roles = roles;
        this.modifiedAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.withdrawnAt = LocalDateTime.now();
    }

    public void encrypt(String secretKey) {
        this.account.encryptEmail(secretKey);
        this.profile.encrypt(secretKey);
    }

    public void decrypt(String secretKey) {
        this.account.decryptEmail(secretKey);
        this.profile.decrypt(secretKey);
    }

    public static PayloadInfo getPayloadInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("인증된 사용자를 찾을 수 없습니다.");
        }

        return (PayloadInfo) authentication.getPrincipal();
    }
}
