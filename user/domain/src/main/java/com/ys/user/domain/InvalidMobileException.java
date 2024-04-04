package com.ys.user.domain;

import com.ys.shared.exception.BadRequestException;

public class InvalidMobileException extends BadRequestException {
    public InvalidMobileException(String message) {
        super(message);
    }
}
