package com.ys.infrastructure.utils;

public interface CommandFactory<R, C> {
    C create(R request);
}
