package com.ys.user.domain;

import com.ys.user.fixture.SupportUserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserEntitiesTest extends SupportUserFixture {
    private UserEntities userEntities;

    @BeforeEach
    void setUp() {
        userEntities = UserEntities.of(List.of(USER_ENTITY));
    }

    @Test
    void 동일한_이메일을_가진_사용자가_존재하면_에러를_반환한다() {
        assertThatThrownBy(()-> userEntities.hasOtherUsersThenEmailDuplicationException())
                .isInstanceOf(EmailDuplicationException.class);
    }

    @Test
    void 동일한_핸드폰_번호를_가진_사용자가_존재하면_에러를_반환한다() {
        assertThatThrownBy(()-> userEntities.hasOtherUsersThenMobileDuplicationException())
                .isInstanceOf(MobileDuplicationException.class);
    }

    @Test
    void 본인_이외에_동일한_핸드폰_번호를_가진_사용자가_존재하면_에러를_반환한다() {
        UserId myUserId = UserId.of(123L);
        assertThatThrownBy(()-> userEntities.hasOtherUsersExceptMeThenMobileDuplicationException(myUserId))
                .isInstanceOf(MobileDuplicationException.class);
    }
}