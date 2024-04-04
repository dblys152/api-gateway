package com.ys.user.domain;

import com.ys.shared.exception.AccessDeniedException;
import com.ys.user.domain.fixture.SupportUserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserTest extends SupportUserFixture {
    private User user;

    @BeforeEach
    void setUp() {
        Account account = Account.of(SupportUserFixture.EMAIL, SupportUserFixture.PASSWORD, SupportUserFixture.OLD_PASSWORD, SupportUserFixture.PASSWORD_MODIFIED_AT, SupportUserFixture.PASSWORD_WRONG_COUNT, SupportUserFixture.LAST_LOGIN_AT);
        Profile profile = Profile.of(SupportUserFixture.USER_NAME, SupportUserFixture.MOBILE, SupportUserFixture.BIRTH_DATE, Gender.MALE.name());
        user = User.of(SupportUserFixture.USER_ID, UserType.USER, UserStatus.JOINED, account, profile, SupportUserFixture.USER_ROLES, SupportUserFixture.NOW, SupportUserFixture.NOW, null);
    }

    @Test
    void 사용자를_생성한다() {
        CreateUserCommand command = new CreateUserCommand(Account.create(SupportUserFixture.EMAIL, SupportUserFixture.PASSWORD), SupportUserFixture.PROFILE);

        User actual = User.create(command);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getType()).isEqualTo(UserType.USER),
                () -> assertThat(actual.getStatus()).isEqualTo(UserStatus.JOINED),
                () -> assertThat(actual.getJoinedAt()).isNotNull(),
                () -> assertThat(actual.getAccount()).isEqualTo(command.getAccount()),
                () -> assertThat(actual.getProfile()).isEqualTo(command.getProfile()),
                () -> assertThat(actual.getRoles()).isEqualTo(SupportUserFixture.USER_ROLES)
        );
    }

    @Test
    void 비밀번호를_변경한다() {
        String newPassword = "abcd99#000";

        user.changePassword(newPassword);

        assertThat(UserPasswordEncoder.matches(newPassword, user.getAccount().getPassword())).isTrue();
    }

    @Test
    void 비밀번호_틀린_횟수를_증가시킨다() {
        int originPasswordWrongCount = user.getAccount().getPasswordWrongCount();

        user.increasePasswordWrongCount();

        assertThat(user.getAccount().getPasswordWrongCount()).isEqualTo(
                originPasswordWrongCount + 1);
    }

    @Test
    void 비밀번호_틀린_횟수가_최대치를_초과하면_에러를_반환한다() {
        IntStream.range(0, Account.MAX_PASSWORD_WRONG_COUNT + 1)
                .forEach(i -> user.increasePasswordWrongCount());

        assertThatThrownBy(() -> user.validateExceededPasswordWrongCount()).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void 비밀번호_틀린_횟수를_초기화한다() {
        user.increasePasswordWrongCount();

        user.initPasswordWrongCount();

        assertThat(user.getAccount().getPasswordWrongCount()).isEqualTo(0);
    }

    @Test
    void 마지막_로그인_일시를_수정하고_비밀번호_틀린_횟수를_초기화한다() {
        user.changeLastLoginAtAndInitPasswordWrongCount();

        assertThat(user.getAccount().getLastLoginAt()).isNotNull();
        assertThat(user.getAccount().getPasswordWrongCount()).isEqualTo(0);
    }

    @Test
    void 사용자_프로필을_수정한다() {
        ChangeUserProfileCommand command = new ChangeUserProfileCommand(SupportUserFixture.PROFILE);

        user.changeProfile(command);

        assertThat(user.getProfile()).isEqualTo(command.getProfile());
    }

    @Test
    void 사용자_권한을_수정한다() {
        String newRoles = UserRole.ROLE_ADMIN.name() + "," + UserRole.ROLE_USER.name();

        user.changeRoles(newRoles);

        assertThat(user.getRoles()).isEqualTo(newRoles);
    }

    @Test
    void 사용자_탈퇴한다() {
        user.withdraw();

        assertThat(user.getWithdrawnAt()).isNotNull();
    }

    @Test
    void 사용자_정보를_암호화한다() {
        user.encrypt(SupportUserFixture.AES_USER_SECRET);
    }

    @Test
    void 사용자_정보를_복호화한다() {
        String email = user.getAccount().getEmail();
        Profile profile = user.getProfile();
        String name = profile.getName();
        String mobile = profile.getMobile();
        String birthDate = profile.getBirthDate();
        String gender = profile.getGender();
        user.encrypt(SupportUserFixture.AES_USER_SECRET);

        user.decrypt(SupportUserFixture.AES_USER_SECRET);

        assertAll(
                () -> assertThat(user.getAccount().getEmail()).isEqualTo(email),
                () -> assertThat(user.getProfile().getName()).isEqualTo(name),
                () -> assertThat(user.getProfile().getMobile()).isEqualTo(mobile),
                () -> assertThat(user.getProfile().getBirthDate()).isEqualTo(birthDate),
                () -> assertThat(user.getProfile().getGender()).isEqualTo(gender)
        );
    }
}