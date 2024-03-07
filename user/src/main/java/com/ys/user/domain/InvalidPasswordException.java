package com.ys.user.domain;

import com.ys.shared.exception.BadRequestException;

public class InvalidPasswordException extends BadRequestException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
