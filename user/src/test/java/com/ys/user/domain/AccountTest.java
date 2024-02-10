package com.ys.user.domain;

import com.ys.infrastructure.exception.AccessDeniedException;
import com.ys.user.fixture.SupportUserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest extends SupportUserFixture {
    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.of(EMAIL, ENCODED_PASSWORD, OLD_PASSWORD, PASSWORD_MODIFIED_AT, PASSWORD_WRONG_COUNT, LAST_LOGIN_AT);
    }

    @Test
    void 계정을_생성한다() {
        Account actual = Account.create(EMAIL, PASSWORD);

        assertThat(actual).isNotNull();
    }

    @ParameterizedTest(name = "email : {0}")
    @MethodSource("getWrongEmails")
    void 잘못된_패턴의_이메일로_계정을_생성하면_에러를_반환한다(String wrongEmail) {
        assertThatThrownBy(() -> Account.create(wrongEmail, PASSWORD)).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> getWrongEmails() {
        return Stream.of(
                Arguments.arguments("test@mail", 1),
                Arguments.arguments("testmail", 2),
                Arguments.arguments("testmail@", 3),
                Arguments.arguments("testmail@mail.", 4)
        );
    }

    @ParameterizedTest(name = "password : {0}")
    @MethodSource("getWrongPasswords")
    void 잘못된_패턴의_비밀번호로_계정을_생성하면_에러를_반환한다(final String wrongPassword) {
        assertThatThrownBy(() -> Account.create(EMAIL, wrongPassword)).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> getWrongPasswords() {
        return Stream.of(
                Arguments.arguments(EMAIL, 1),
                Arguments.arguments("abcd12345", 2),
                Arguments.arguments("abcd12@", 3),
                Arguments.arguments("1234567#@", 4),
                Arguments.arguments("123456789", 5),
                Arguments.arguments("abcdefghijk", 6),
                Arguments.arguments("!\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~", 7)
        );
    }

    @Test
    void 비밀번호를_변경한다() {
        String newPassword = "abcd99#000";

        account.changePassword(newPassword);

        assertThat(UserPasswordEncoder.matches(newPassword, account.getPassword())).isTrue();
    }

    @Test
    void 비밀번호_틀린_횟수를_증가시킨다() {
        int originPasswordWrongCount = account.getPasswordWrongCount();

        account.increasePasswordWrongCount();

        assertThat(account.getPasswordWrongCount()).isEqualTo(
                originPasswordWrongCount + 1);
    }

    @Test
    void 비밀번호_틀린_횟수를_초기화한다() {
        account.increasePasswordWrongCount();
        account.increasePasswordWrongCount();

        account.initPasswordWrongCount();

        assertThat(account.getPasswordWrongCount()).isEqualTo(0);
    }

    @Test
    void 비밀번호_틀린_횟수가_최대치를_초과하면_에러를_반환한다() {
        IntStream.range(0, Account.MAX_PASSWORD_WRONG_COUNT + 1)
                .forEach(i -> account.increasePasswordWrongCount());

        assertThatThrownBy(() -> account.validateExceededPasswordWrongCount()).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void 마지막_로그인_일시를_수정한다() {
        account.modifyLastLoginAt(NOW);

        assertThat(account.getLastLoginAt()).isEqualTo(NOW);
    }

    @Test
    void 이메일을_암호화한다() {
        account.encryptEmail(AES_USER_SECRET);
    }

    @Test
    void 이메일을_복호화한다() {
        String email = account.getEmail();

        account.encryptEmail(AES_USER_SECRET);

        account.decryptEmail(AES_USER_SECRET);

        assertThat(account.getEmail()).isEqualTo(email);
    }
}