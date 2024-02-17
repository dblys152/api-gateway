package com.ys.user.application.service;

import com.ys.infrastructure.event.DomainEventPublisher;
import com.ys.infrastructure.utils.EventFactory;
import com.ys.user.application.port.in.ChangeUserPasswordUseCase;
import com.ys.user.application.port.in.ChangeUserProfileUseCase;
import com.ys.user.application.port.in.SignUpUseCase;
import com.ys.user.application.port.in.WithdrawUserUseCase;
import com.ys.user.application.port.out.LoadUserPort;
import com.ys.user.application.port.out.RecordUserPort;
import com.ys.user.domain.*;
import com.ys.user.domain.event.UserEvent;
import com.ys.user.domain.event.UserEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserCommandService implements
        SignUpUseCase,
        ChangeUserProfileUseCase,
        ChangeUserPasswordUseCase,
        WithdrawUserUseCase {
    private final LoadUserPort loadUserPort;
    private final RecordUserPort recordUserPort;
    private final EventFactory<User, UserEvent> eventFactory;
    private final DomainEventPublisher<UserEvent> domainEventPublisher;

    @Override
    public User signUp(CreateUserCommand command) {
        validateDuplicatedEmail(command.getAccount().getEmail());
        validateDuplicatedMobile(command.getProfile().getMobile());

        User user = User.create(command);
        recordUserPort.insert(user);

        domainEventPublisher.publish(
                UserEventType.USER_SIGNUP_COMPLETED_EVENT.name(), eventFactory.create(user), user.getJoinedAt());

        return user;
    }

    private void validateDuplicatedEmail(String email) {
        Users foundUsersByEmail = loadUserPort.selectAllByEmailAndWithdrawnAtIsNull(email);
        foundUsersByEmail.throwEmailDuplicationExceptionIfOtherUsersExist();
    }

    private void validateDuplicatedMobile(String mobile) {
        Users foundUsersByMobile = loadUserPort.selectAllByMobileAndWithdrawnAtIsNull(mobile);
        foundUsersByMobile.throwMobileDuplicationExceptionIfOtherUsersExist();
    }

    @Override
    public User change(UserId userId, ChangeUserProfileCommand command) {
        User user = loadUserPort.selectOneByIdAndWithdrawnAtIsNull(userId);
        validateDuplicatedMobileByUserId(command.getProfile().getMobile(), userId);

        user.changeProfile(command);
        recordUserPort.updateByProfile(user);

        domainEventPublisher.publish(
                UserEventType.USER_PROFILE_CHANGED_EVENT.name(), eventFactory.create(user), user.getModifiedAt());

        return user;
    }

    private void validateDuplicatedMobileByUserId(String mobile, UserId userId) {
        Users foundUsersByMobile = loadUserPort.selectAllByMobileAndWithdrawnAtIsNull(mobile);
        foundUsersByMobile.throwMobileDuplicationExceptionIfOtherUsersExistThanMe(userId);
    }

    @Override
    public void change(UserId userId, String password) {
        User user = loadUserPort.selectOneByIdAndWithdrawnAtIsNull(userId);

        user.changePassword(password);
        recordUserPort.updateByPassword(user);

        domainEventPublisher.publish(
                UserEventType.USER_PASSWORD_CHANGED_EVENT.name(), eventFactory.create(user), user.getModifiedAt());
    }

    @Override
    public void withdraw(UserId userId) {
        User user = loadUserPort.selectOneByIdAndWithdrawnAtIsNull(userId);

        user.withdraw();
        recordUserPort.updateByWithdrawnAt(user);

        domainEventPublisher.publish(
                UserEventType.USER_WITHDRAWN_EVENT.name(), eventFactory.create(user), user.getWithdrawnAt());
    }
}
