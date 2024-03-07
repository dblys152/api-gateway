package com.ys.user.domain;

import com.ys.shared.encryption.AESEncryptor;
import com.ys.shared.encryption.TwoWayEncryptor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Profile {
    public static final String MOBILE_REGEX = "\\d{3,4}-\\d{4}-\\d{4}";

    @NotNull
    @Size(min = 1, max = 128)
    private String name;

    @NotNull
    @Size(min = 1, max = 128)
    private String mobile;

    @Size(min = 1, max = 128)
    private String birthDate;

    @Size(min = 1, max = 128)
    private String gender;

    protected Profile(String name, String mobile, String birthDate, String gender) {
        this.name = name;
        this.mobile = mobile;
        this.birthDate = birthDate;
        this.gender = gender;
        validate();
    }

    public static Profile of(String name, String mobile, String birthDate, String gender) {
        return new Profile(name, mobile, birthDate, gender);
    }

    private void validate() {
        validateBirthDate();
        validateGender();
        validateMobile();
    }

    private void validateBirthDate() {
        if (!StringUtils.isEmpty(this.birthDate)) {
            try {
                LocalDate.parse(this.birthDate, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeException ex) {
                throw new IllegalArgumentException("생년월일의 형식이 올바르지 않습니다. (yyyy-MM-dd)");
            }
        }
    }

    private void validateGender() {
        if (!StringUtils.isEmpty(this.gender)) {
            try {
                Gender.valueOf(this.gender);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("성별 값이 올바르지 않습니다.");
            }
        }
    }

    private void validateMobile() {
        if (!this.mobile.matches(MOBILE_REGEX)) {
            throw new InvalidMobileException("핸드폰 번호 패턴이 올바르지 않습니다. (0xx-xxxx-xxxx)");
        }
    }

    public void encrypt(String secretKey) {
        TwoWayEncryptor twoWayEncryptor = AESEncryptor.getInstance();
        this.name = twoWayEncryptor.encrypt(this.name, secretKey);
        this.mobile = twoWayEncryptor.encrypt(this.mobile, secretKey);
        this.birthDate = twoWayEncryptor.encrypt(this.birthDate, secretKey);
        this.gender = twoWayEncryptor.encrypt(this.gender, secretKey);
    }

    public void decrypt(String secretKey) {
        TwoWayEncryptor twoWayEncryptor = AESEncryptor.getInstance();
        this.name = twoWayEncryptor.decrypt(this.name, secretKey);
        this.mobile = twoWayEncryptor.decrypt(this.mobile, secretKey);
        this.birthDate = twoWayEncryptor.decrypt(this.birthDate, secretKey);
        this.gender = twoWayEncryptor.decrypt(this.gender, secretKey);
    }
}
