package com.ys.user.domain;

import com.ys.shared.exception.BadRequestException;

public class EmailDuplicationException extends BadRequestException {
    private static final String EXCEPTION_MESSAGE = "이미 존재하는 이메일 입니다.";

    public EmailDuplicationException() {
        super(EXCEPTION_MESSAGE);
    }
}
