package com.ys.user.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class UserEntity {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

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

    public static UserEntity of(
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
        return new UserEntity(userId, type, status, account, profile, roles, joinedAt, modifiedAt, withdrawnAt);
    }

    public static UserEntity create(UserId userId, CreateUserCommand command) {
        LocalDateTime now = LocalDateTime.now();
        return new UserEntity(
                userId,
                UserType.USER,
                UserStatus.JOINED,
                command.getAccount(),
                command.getProfile(),
                ROLE_USER,
                now,
                now,
                null
        );
    }

    public boolean matchesPassword(String password) {
        return this.account.matchesPassword(password);
    }

    public void modifyPassword(String password) {
        this.account.modifyPassword(password);
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

    public void modifyLastLoginAtAndInitPasswordWrongCount() {
        LocalDateTime now = LocalDateTime.now();
        this.account.modifyLastLoginAt(now);
        this.account.initPasswordWrongCount();
        this.modifiedAt = now;
    }

    public void modifyProfile(ChangeUserProfileCommand command) {
        this.profile = command.getProfile();
        this.modifiedAt = LocalDateTime.now();
    }

    public void modifyRoles(String roles) {
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
}
