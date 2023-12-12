package com.ys.user.domain;

import com.ys.infrastructure.exception.BadRequestException;

public class MatchedOldPasswordException extends BadRequestException {
    private static final String MESSAGE = "이전 비밀번호와 일치합니다.";

    public MatchedOldPasswordException() {
        super(MESSAGE);
    }
}
