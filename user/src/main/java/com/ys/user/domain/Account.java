package com.ys.user.domain;

import com.ys.shared.encryption.AESEncryptor;
import com.ys.shared.exception.AccessDeniedException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDateTime;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Account {
    public static final int MIN_PASSWORD_LENGTH = 9;
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~])[A-Za-z\\d!\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~]{8,12}$";
    public static final int ZERO_PASSWORD_WRONG_COUNT = 0;
    public static final int MAX_PASSWORD_WRONG_COUNT = 10;

    @NotNull
    @Size(min = 8, max = 128)
    private String email;

    @NotNull
    @Size(min = 1, max = 255)
    private String password;

    @Size(min = 1, max = 255)
    private String oldPassword;

    private LocalDateTime passwordModifiedAt;

    private int passwordWrongCount;

    private LocalDateTime lastLoginAt;

    public static Account of(
            String email,
            String password,
            String oldPassword,
            LocalDateTime passwordModifiedAt,
            int passwordWrongCount,
            LocalDateTime lastLoginAt
    ) {
        return new Account(email, password, oldPassword, passwordModifiedAt, passwordWrongCount, lastLoginAt);
    }

    public static Account create(String email, String password) {
        validateEmail(email);
        validatePassword(email, password);
        String encodedPassword = UserPasswordEncoder.encode(password);
        return new Account(
                email,
                encodedPassword,
                encodedPassword,
                LocalDateTime.now(),
                ZERO_PASSWORD_WRONG_COUNT,
                null
        );
    }

    private static void validateEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new InvalidEmailException("이메일 패턴이 올바르지 않습니다.");
        }
    }

    private static void validatePassword(String email, String password) {
        if (email.equals(password)) {
            throw new InvalidPasswordException("비밀번호를 이메일과 동일하게 지정할 수 없습니다.");
        }
        if (StringUtils.isEmpty(password)
                || !password.matches(PASSWORD_REGEX)) {
            throw new InvalidPasswordException("비밀번호는 각각 1개 이상의 영문, 숫자, 특수문자를 포함한 8~12자리여야 합니다.");
        }
    }

    public boolean matchesPassword(String password) {
        return UserPasswordEncoder.matches(password, this.password);
    }

    public void changePassword(String password) {
        validatePassword(this.email, password);
        validateMatchOldPassword(password);
        this.oldPassword = this.password;
        this.password = UserPasswordEncoder.encode(password);
    }

    private void validateMatchOldPassword(String password) {
        if (UserPasswordEncoder.matches(password, this.oldPassword)) {
            throw new InvalidPasswordException("이전 비밀번호와 일치합니다.");
        }
    }

    public void increasePasswordWrongCount() {
        this.passwordWrongCount++;
    }

    public void initPasswordWrongCount() {
        this.passwordWrongCount = ZERO_PASSWORD_WRONG_COUNT;
    }

    public void validateExceededPasswordWrongCount() {
        if (this.passwordWrongCount > MAX_PASSWORD_WRONG_COUNT) {
            throw new AccessDeniedException("비밀번호 틀린 횟수가 " + MAX_PASSWORD_WRONG_COUNT + "회를 초과하였습니다.");
        }
    }

    public void modifyLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void encryptEmail(String secretKey) {
        this.email = AESEncryptor.getInstance().encrypt(this.email, secretKey);
    }

    public void decryptEmail(String secretKey) {
        this.email = AESEncryptor.getInstance().decrypt(this.email, secretKey);

    }
}
