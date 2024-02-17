package com.ys.user.domain;

import com.ys.infrastructure.exception.BadRequestException;

public class InvalidPasswordException extends BadRequestException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
