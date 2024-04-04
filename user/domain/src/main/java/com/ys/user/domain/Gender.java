package com.ys.user.domain;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남"),
    FEMALE("여"),
    NONE("알 수 없음");

    final String description;

    Gender(final String description) {
        this.description = description;
    }
}
