package com.ys.user.domain;

import com.ys.infrastructure.exception.AccessDeniedException;
import com.ys.user.fixture.SupportUserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest extends SupportUserFixture {
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        Account account = Account.of(EMAIL, PASSWORD, OLD_PASSWORD, PASSWORD_MODIFIED_AT, PASSWORD_WRONG_COUNT, LAST_LOGIN_AT);
        Profile profile = Profile.of(USER_NAME, MOBILE, BIRTH_DATE, Gender.MALE.name());
        userEntity = UserEntity.of(USER_ID, UserType.USER, UserStatus.JOINED, account, profile, USER_ROLES, NOW, NOW, null);
    }

    @Test
    void 사용자를_생성한다() {
        CreateUserCommand command = new CreateUserCommand(Account.create(EMAIL, PASSWORD), PROFILE);

        UserEntity actual = UserEntity.create(USER_ID, command);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getUserId()).isEqualTo(USER_ID),
                () -> assertThat(actual.getType()).isEqualTo(UserType.USER),
                () -> assertThat(actual.getStatus()).isEqualTo(UserStatus.JOINED),
                () -> assertThat(actual.getJoinedAt()).isNotNull(),
                () -> assertThat(actual.getAccount()).isEqualTo(command.getAccount()),
                () -> assertThat(actual.getProfile()).isEqualTo(command.getProfile()),
                () -> assertThat(actual.getRoles()).isEqualTo(USER_ROLES)
        );
    }

    @Test
    void 비밀번호를_변경한다() {
        String newPassword = "abcd99#000";

        userEntity.modifyPassword(newPassword);

        assertThat(UserPasswordEncoder.matches(newPassword, userEntity.getAccount().getPassword())).isTrue();
    }

    @Test
    void 비밀번호_틀린_횟수를_증가시킨다() {
        int originPasswordWrongCount = userEntity.getAccount().getPasswordWrongCount();

        userEntity.increasePasswordWrongCount();

        assertThat(userEntity.getAccount().getPasswordWrongCount()).isEqualTo(
                originPasswordWrongCount + 1);
    }

    @Test
    void 비밀번호_틀린_횟수가_최대치를_초과하면_에러를_반환한다() {
        IntStream.range(0, Account.MAX_PASSWORD_WRONG_COUNT + 1)
                .forEach(i -> userEntity.increasePasswordWrongCount());

        assertThatThrownBy(() -> userEntity.validateExceededPasswordWrongCount()).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void 비밀번호_틀린_횟수를_초기화한다() {
        userEntity.increasePasswordWrongCount();

        userEntity.initPasswordWrongCount();

        assertThat(userEntity.getAccount().getPasswordWrongCount()).isEqualTo(0);
    }

    @Test
    void 마지막_로그인_일시를_수정하고_비밀번호_틀린_횟수를_초기화한다() {
        userEntity.modifyLastLoginAtAndInitPasswordWrongCount();

        assertThat(userEntity.getAccount().getLastLoginAt()).isNotNull();
        assertThat(userEntity.getAccount().getPasswordWrongCount()).isEqualTo(0);
    }

    @Test
    void 사용자_프로필을_수정한다() {
        ChangeUserProfileCommand command = new ChangeUserProfileCommand(PROFILE);

        userEntity.modifyProfile(command);

        assertThat(userEntity.getProfile()).isEqualTo(command.getProfile());
    }

    @Test
    void 사용자_권한을_수정한다() {
        String newRoles = UserEntity.ROLE_ADMIN + "," + UserEntity.ROLE_USER;

        userEntity.modifyRoles(newRoles);

        assertThat(userEntity.getRoles()).isEqualTo(newRoles);
    }

    @Test
    void 사용자_탈퇴한다() {
        userEntity.withdraw();

        assertThat(userEntity.getWithdrawnAt()).isNotNull();
    }

    @Test
    void 사용자_정보를_암호화한다() {
        userEntity.encrypt(AES_USER_SECRET);
    }

    @Test
    void 사용자_정보를_복호화한다() {
        String email = userEntity.getAccount().getEmail();
        Profile profile = userEntity.getProfile();
        String name = profile.getName();
        String mobile = profile.getMobile();
        String birthDate = profile.getBirthDate();
        String gender = profile.getGender();
        userEntity.encrypt(AES_USER_SECRET);

        userEntity.decrypt(AES_USER_SECRET);

        assertAll(
                () -> assertThat(userEntity.getAccount().getEmail()).isEqualTo(email),
                () -> assertThat(userEntity.getProfile().getName()).isEqualTo(name),
                () -> assertThat(userEntity.getProfile().getMobile()).isEqualTo(mobile),
                () -> assertThat(userEntity.getProfile().getBirthDate()).isEqualTo(birthDate),
                () -> assertThat(userEntity.getProfile().getGender()).isEqualTo(gender)
        );
    }
}