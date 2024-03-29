CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID             BIGINT  PRIMARY KEY,
    TYPE                VARCHAR(30)     NOT NULL,
    STATUS              VARCHAR(30)     NOT NULL,
    EMAIL               VARCHAR(128)    NOT NULL,
    PASSWORD            VARCHAR(256)    NOT NULL,
    OLD_PASSWORD        VARCHAR(256),
    PASSWORD_MODIFIED_AT    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    PASSWORD_WRONG_COUNT    INTEGER     NOT NULL DEFAULT 0,
    LAST_LOGIN_AT       TIMESTAMP WITH TIME ZONE,
    NAME                VARCHAR(128)    NOT NULL,
    MOBILE              VARCHAR(128)    NOT NULL,
    BIRTH_DATE          VARCHAR(128),
    GENDER              VARCHAR(128),
    ROLES               TEXT,
    JOINED_AT           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    MODIFIED_AT         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    WITHDRAWN_AT        TIMESTAMP WITH TIME ZONE
);