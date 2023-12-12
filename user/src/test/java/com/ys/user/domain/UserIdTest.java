package com.ys.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {
    @Test
    void 동등성_비교() {
        UserId userId = UserId.of(1L);
        UserId userId2 = UserId.of(1L);

        assertThat(userId).isEqualTo(userId2);
    }
}