package com.ys.authentication.adapter.out;

import com.ys.authentication.adapter.out.persistence.UserRepository;
import com.ys.authentication.application.port.out.LoadUserPort;
import com.ys.authentication.application.port.out.RecordUserPort;
import com.ys.shared.encryption.AESEncryptor;
import com.ys.user.domain.User;
import com.ys.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort, RecordUserPort {
    @Value("${aes.user-secret}")
    private String AES_USER_SECRET;

    private final UserRepository repository;

    @Override
    public User selectOneByIdAndWithdrawnAtIsNull(UserId userId) {
        return repository.selectOneByIdAndWithdrawnAtIsNull(userId)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public User selectOneByEmailAndWithdrawnAtIsNull(String email) {
        String encryptedEmail = AESEncryptor.getInstance().encrypt(email, AES_USER_SECRET);
        return repository.selectOneByEmailAndWithdrawnAtIsNull(encryptedEmail)
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
