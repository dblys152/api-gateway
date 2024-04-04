package com.ys.user.domain;

import com.ys.shared.exception.BadRequestException;

public class InvalidEmailException extends BadRequestException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
