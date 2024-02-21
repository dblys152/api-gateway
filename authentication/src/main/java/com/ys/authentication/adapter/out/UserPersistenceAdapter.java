package com.ys.authentication.adapter.out;

import com.ys.authentication.adapter.out.persistence.UserRepository;
import com.ys.authentication.application.port.out.LoadUserPort;
import com.ys.authentication.application.port.out.RecordUserPort;
import com.ys.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort, RecordUserPort {
    private final UserRepository repository;

    @Override
    public User selectOneByEmailAndWithdrawnAtIsNull(String email) {
        return repository.selectOneByEmailAndWithdrawnAtIsNull(email)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void updateByPasswordWrongCount(User user) {
        repository.updateByPasswordWrongCount(user);
    }

    @Override
    public void updateByLastLoginAtAndPasswordWrongCount(User user) {
        repository.updateByLastLoginAtAndPasswordWrongCount(user);
    }
}
