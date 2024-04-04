package com.ys.user.adapter.infrastructure.persistence.fixture;

import com.ys.user.domain.*;

import java.time.LocalDateTime;

public class SupportUserFixture {
    protected static final String AES_USER_SECRET = "RENULVRFU1QtRk9SLUVOQ1JZUFRJT04K";
    protected static final LocalDateTime NOW = LocalDateTime.now();
    protected static final UserId USER_ID = UserId.of(999999999L);
    protected static final String EMAIL = "test@devmon.co.kr";
    protected static final String PASSWORD = "abc007#!";
    protected static final String ENCODED_PASSWORD = "$2a$10$SeJMmqCEQeo85Uf9bn1Glex8kpNU5/PKbAcbLAbVoTOznXWLAWYkW";
    protected static final String OLD_PASSWORD = "$2a$10$SeJMmqCEQeo85Uf9bn1Glex8kpNU5/PKbAcbLAbVoTOznXWLAWYkW";
    protected static final LocalDateTime PASSWORD_MODIFIED_AT = NOW;
    protected static final int PASSWORD_WRONG_COUNT = 0;
    protected static final LocalDateTime LAST_LOGIN_AT = NOW.plusMinutes(3);
    protected static final Account ACCOUNT = Account.of(
            EMAIL, ENCODED_PASSWORD, OLD_PASSWORD, PASSWORD_MODIFIED_AT, PASSWORD_WRONG_COUNT, LAST_LOGIN_AT);
    protected static final String USER_NAME = "USER_NAME";
    protected static final String MOBILE = "010-1234-1234";
    protected static final String BIRTH_DATE = "1995-02-25";
    protected static final Profile PROFILE = Profile.of(USER_NAME, MOBILE, BIRTH_DATE, Gender.MALE.name());
    protected static final String USER_ROLES = "ROLE_USER";
    protected static final User USER_ENTITY = User.of(
            USER_ID,
            UserType.USER,
            UserStatus.JOINED,
            Account.of(EMAIL, PASSWORD, OLD_PASSWORD, PASSWORD_MODIFIED_AT, PASSWORD_WRONG_COUNT, LAST_LOGIN_AT),
            Profile.of(USER_NAME, MOBILE, BIRTH_DATE, Gender.MALE.name()),
            USER_ROLES,
            NOW,
            NOW,
            null
    );
}
