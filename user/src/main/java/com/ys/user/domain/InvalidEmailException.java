package com.ys.user.domain;

import com.ys.infrastructure.exception.BadRequestException;

public class InvalidEmailException extends BadRequestException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
