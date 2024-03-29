CREATE TABLE IF NOT EXISTS AUTHENTICATION_INFOS
(
    AUTHENTICATION_INFO_ID    BIGINT       PRIMARY KEY,
    USER_ID             BIGINT          NOT NULL,
    REFRESH_TOKEN       VARCHAR(512)    NOT NULL,
    EXPIRED_AT          TIMESTAMP WITH TIME ZONE NOT NULL,
    CLIENT_IP           VARCHAR(30)     NOT NULL,
    CREATED_AT          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);