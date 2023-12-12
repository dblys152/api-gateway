package com.ys.user.domain;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomPasswordGenerator {
    private static final int MIN_RANDOM_PASSWORD_LENGTH = 4;
    private static final String ENGLISH_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()";
    private static final boolean USE_LETTERS = true;
    private static final boolean USE_NUMBERS = true;

    public String generate(int count) {
        validate(count);

        int unitLength = 1;
        String randomEnglishLetter = RandomStringUtils.random(unitLength, ENGLISH_LETTERS);
        String randomDigit = RandomStringUtils.random(unitLength, DIGITS);
        String randomSpecialCharacter = RandomStringUtils.random(unitLength, SPECIAL_CHARACTERS);

        int totalUnitLength = unitLength * 3;
        String randomString = RandomStringUtils.random(count - totalUnitLength, USE_LETTERS, USE_NUMBERS);

        return randomEnglishLetter + randomDigit + randomSpecialCharacter + randomString;
    }

    private void validate(int count) {
        if (count < MIN_RANDOM_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(String.format("랜덤 비밀번호를 %s보다 작거나 같은 길이로 생성할 수 없습니다.", MIN_RANDOM_PASSWORD_LENGTH));
        }
        if (count < Account.MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("비밀번호 최소 길이보다 짧은 값으로 랜덤 비밀번호를 생성할 수 없습니다.");
        }
    }
}
