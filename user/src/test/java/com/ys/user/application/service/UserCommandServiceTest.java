package com.ys.user.application.service;

import com.ys.infrastructure.event.DomainEventPublisher;
import com.ys.infrastructure.utils.EventFactory;
import com.ys.user.application.port.out.LoadUserPort;
import com.ys.user.application.port.out.RecordUserPort;
import com.ys.user.domain.*;
import com.ys.user.domain.event.UserEvent;
import com.ys.user.domain.event.UserEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {
    private static final String ANY_EMAIL = "test@mail.com";
    private static final String ANY_MOBILE = "010-7777-8888";
    private static final UserId ANY_USER_ID = UserId.of(9999999999L);
    private static final String ANY_PASSWORD = "asdf1234%";

    @InjectMocks
    private UserCommandService sut;

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private RecordUserPort recordUserPort;
    @Mock
    private EventFactory<User, UserEvent> eventFactory;
    @Mock
    private DomainEventPublisher<UserEvent> domainEventPublisher;

    @Test
    void 회원_가입_한다() {
        CreateUserCommand command = getCreateUserCommand();

        given(loadUserPort.selectAllByEmailAndWithdrawnAtIsNull(ANY_EMAIL)).willReturn(mock(Users.class));
        given(loadUserPort.selectAllByMobileAndWithdrawnAtIsNull(ANY_MOBILE)).willReturn(mock(Users.class));
        UserEvent userEvent = mock(UserEvent.class);
        given(eventFactory.create(any(User.class))).willReturn(userEvent);

        User actual = sut.signUp(command);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> then(loadUserPort).should().selectAllByEmailAndWithdrawnAtIsNull(ANY_EMAIL),
                () -> then(loadUserPort).should().selectAllByMobileAndWithdrawnAtIsNull(ANY_MOBILE),
                () -> then(recordUserPort).should().insert(any(User.class)),
                () -> then(eventFactory).should().create(any(User.class)),
                () -> then(domainEventPublisher).should().publish(eq(UserEventType.USER_SIGNUP_COMPLETED_EVENT.name()), eq(userEvent), any(LocalDateTime.class))
        );
    }

    private CreateUserCommand getCreateUserCommand() {
        CreateUserCommand command = mock(CreateUserCommand.class);

        Account account = mock(Account.class);
        given(command.getAccount()).willReturn(account);
        given(account.getEmail()).willReturn(ANY_EMAIL);

        Profile profile = mock(Profile.class);
        given(command.getProfile()).willReturn(profile);
        given(profile.getMobile()).willReturn(ANY_MOBILE);

        return command;
    }

    @Test
    void 프로필을_변경한다() {
        ChangeUserProfileCommand command = getChangeUserProfileCommand();
        User mockUser = mock(User.class);
        given(loadUserPort.selectOneByIdAndWithdrawnAtIsNull(ANY_USER_ID)).willReturn(mockUser);
        given(loadUserPort.selectAllByMobileAndWithdrawnAtIsNull(ANY_MOBILE)).willReturn(mock(Users.class));
        UserEvent userEvent = mock(UserEvent.class);
        given(eventFactory.create(any(User.class))).willReturn(userEvent);

        User actual = sut.change(ANY_USER_ID, command);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> then(loadUserPort).should().selectOneByIdAndWithdrawnAtIsNull(ANY_USER_ID),
                () -> then(mockUser).should().changeProfile(command),
                () -> then(recordUserPort).should().updateByProfile(mockUser),
                () -> then(eventFactory).should().create(mockUser),
                () -> then(domainEventPublisher).should().publish(UserEventType.USER_PROFILE_CHANGED_EVENT.name(), userEvent, mockUser.getModifiedAt())
        );
    }

    @Test
    void 비밀번호를_변경한다() {
        User mockUser = mock(User.class);
        given(loadUserPort.selectOneByIdAndWithdrawnAtIsNull(ANY_USER_ID)).willReturn(mockUser);
        UserEvent userEvent = mock(UserEvent.class);
        given(eventFactory.create(any(User.class))).willReturn(userEvent);

        sut.change(ANY_USER_ID, ANY_PASSWORD);

        assertAll(
                () -> then(loadUserPort).should().selectOneByIdAndWithdrawnAtIsNull(ANY_USER_ID),
                () -> then(mockUser).should().changePassword(ANY_PASSWORD),
                () -> then(recordUserPort).should().updateByPassword(mockUser),
                () -> then(eventFactory).should().create(mockUser),
                () -> then(domainEventPublisher).should().publish(UserEventType.USER_PASSWORD_CHANGED_EVENT.name(), userEvent, mockUser.getModifiedAt())
        );
    }

    private ChangeUserProfileCommand getChangeUserProfileCommand() {
        ChangeUserProfileCommand command = mock(ChangeUserProfileCommand.class);

        Profile profile = mock(Profile.class);
        given(command.getProfile()).willReturn(profile);
        given(profile.getMobile()).willReturn(ANY_MOBILE);

        return command;
    }

    @Test
    void 회원_탈퇴한다() {
        User mockUser = mock(User.class);
        given(loadUserPort.selectOneByIdAndWithdrawnAtIsNull(ANY_USER_ID)).willReturn(mockUser);
        UserEvent userEvent = mock(UserEvent.class);
        given(eventFactory.create(any(User.class))).willReturn(userEvent);

        sut.withdraw(ANY_USER_ID);

        assertAll(
                () -> then(loadUserPort).should().selectOneByIdAndWithdrawnAtIsNull(ANY_USER_ID),
                () -> then(mockUser).should().withdraw(),
                () -> then(recordUserPort).should().updateByWithdrawnAt(mockUser),
                () -> then(eventFactory).should().create(mockUser),
                () -> then(domainEventPublisher).should().publish(UserEventType.USER_WITHDRAWN_EVENT.name(), userEvent, mockUser.getWithdrawnAt())
        );
    }
}