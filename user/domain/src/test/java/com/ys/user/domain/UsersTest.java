package com.ys.user.domain;

import com.ys.user.domain.fixture.SupportUserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsersTest extends SupportUserFixture {
    private Users users;

    @BeforeEach
    void setUp() {
        users = Users.of(List.of(SupportUserFixture.USER_ENTITY));
    }

    @Test
    void 동일한_이메일을_가진_사용자가_존재하면_에러를_반환한다() {
        assertThatThrownBy(()-> users.throwEmailDuplicationExceptionIfOtherUsersExist())
                .isInstanceOf(EmailDuplicationException.class);
    }

    @Test
    void 동일한_핸드폰_번호를_가진_사용자가_존재하면_에러를_반환한다() {
        assertThatThrownBy(()-> users.throwMobileDuplicationExceptionIfOtherUsersExist())
                .isInstanceOf(MobileDuplicationException.class);
    }

    @Test
    void 본인_이외에_동일한_핸드폰_번호를_가진_사용자가_존재하면_에러를_반환한다() {
        UserId myUserId = UserId.of(123L);
        assertThatThrownBy(()-> users.throwMobileDuplicationExceptionIfOtherUsersExistThanMe(myUserId))
                .isInstanceOf(MobileDuplicationException.class);
    }
}