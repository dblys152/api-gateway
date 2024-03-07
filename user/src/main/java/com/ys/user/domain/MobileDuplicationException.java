package com.ys.user.domain;

import com.ys.shared.exception.BadRequestException;

public class MobileDuplicationException extends BadRequestException {
    private static final String EXCEPTION_MESSAGE = "이미 존재하는 핸드폰 번호 입니다.";

    public MobileDuplicationException() {
        super(EXCEPTION_MESSAGE);
    }
}
