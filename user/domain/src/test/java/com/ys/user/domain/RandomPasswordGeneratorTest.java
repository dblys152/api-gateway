package com.ys.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RandomPasswordGeneratorTest {
    private RandomPasswordGenerator randomPasswordGenerator;

    @BeforeEach
    void setUp() {
        randomPasswordGenerator = new RandomPasswordGenerator();
    }

    @Test
    void 랜덤_비밀번호를_생성한다() {
        String actual = randomPasswordGenerator.generate(Account.MIN_PASSWORD_LENGTH);

        assertThat(actual).isNotNull();
        assertThat(actual.length()).isEqualTo(Account.MIN_PASSWORD_LENGTH);
    }
}