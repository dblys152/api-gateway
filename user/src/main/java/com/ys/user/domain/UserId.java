package com.ys.user.domain;

import com.ys.infrastructure.data.LongId;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserId implements LongId {
    @NotNull
    private Long id;

    @Override
    public Long get() {
        return this.id;
    }

    public static UserId of(Long id) {
        return new UserId(id);
    }
}
