package com.ys.user.domain;

import com.ys.infrastructure.domain.LongId;
import lombok.Value;

@Value(staticConstructor = "of")
public class UserId implements LongId {
    Long id;

    @Override
    public Long get() {
        return this.id;
    }

    public static UserId getEmptyUserId() {
        return new UserId(null);
    }
}
