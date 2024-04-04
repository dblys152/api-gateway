package com.ys.user.domain;

import com.ys.user.domain.fixture.SupportUserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProfileTest extends SupportUserFixture {
    private Profile profile;

    @BeforeEach
    void setUp() {
        profile = Profile.of(SupportUserFixture.USER_NAME, SupportUserFixture.MOBILE, SupportUserFixture.BIRTH_DATE, Gender.MALE.name());
    }

    @Test
    void 프로필을_생성한다() {
        Profile actual = Profile.of(SupportUserFixture.USER_NAME, SupportUserFixture.MOBILE, SupportUserFixture.BIRTH_DATE, Gender.MALE.name());

        assertThat(actual).isNotNull();
    }

    @ParameterizedTest(name = "mobile : {0}")
    @MethodSource("getWrongMobiles")
    void 잘못된_패턴의_모바일로_프로필을_생성하면_에러를_반환한다(String wrongMobile) {
        assertThatThrownBy(() -> Profile.of(SupportUserFixture.USER_NAME, wrongMobile, SupportUserFixture.BIRTH_DATE, Gender.MALE.name()))
                .isInstanceOf(InvalidMobileException.class);
    }

    private static Stream<Arguments> getWrongMobiles() {
        return Stream.of(
                Arguments.arguments("010-12341-234", 1),
                Arguments.arguments("01077778888", 2),
                Arguments.arguments("010-77778888", 3)
        );
    }

    @Test
    void 잘못된_생년월일_형식으로_프로필을_생성하면_에러를_반환한다() {
        assertThatThrownBy(() -> Profile.of(SupportUserFixture.USER_NAME, SupportUserFixture.MOBILE, "19950225", Gender.MALE.name())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 잘못된_성별로_프로필을_생성하면_에러를_반환한다() {
        assertThatThrownBy(() -> Profile.of(SupportUserFixture.USER_NAME, SupportUserFixture.MOBILE, SupportUserFixture.BIRTH_DATE, "ETC")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 프로필을_암호화한다() {
        profile.encrypt(SupportUserFixture.AES_USER_SECRET);
    }

    @Test
    void 프로필을_복호화한다() {
        String name = profile.getName();
        String mobile = profile.getMobile();
        String birthDate = profile.getBirthDate();
        String gender = profile.getGender();
        profile.encrypt(SupportUserFixture.AES_USER_SECRET);

        profile.decrypt(SupportUserFixture.AES_USER_SECRET);

        assertAll(
                () -> assertThat(profile.getName()).isEqualTo(name),
                () -> assertThat(profile.getMobile()).isEqualTo(mobile),
                () -> assertThat(profile.getBirthDate()).isEqualTo(birthDate),
                () -> assertThat(profile.getGender()).isEqualTo(gender)
        );
    }
}