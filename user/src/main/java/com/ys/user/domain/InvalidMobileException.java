package com.ys.user.domain;

import com.ys.infrastructure.exception.BadRequestException;

public class InvalidMobileException extends BadRequestException {
    public InvalidMobileException(String message) {
        super(message);
    }
}
