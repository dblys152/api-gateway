package com.ys.user.adapter.infrastructure;

import com.ys.shared.encryption.AESEncryptor;
import com.ys.user.adapter.infrastructure.persistence.UserRepository;
import com.ys.user.application.port.out.LoadUserPort;
import com.ys.user.application.port.out.RecordUserPort;
import com.ys.user.domain.User;
import com.ys.user.domain.UserId;
import com.ys.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements RecordUserPort, LoadUserPort {
    @Value("${aes.user-secret}")
    private String AES_USER_SECRET;

    private final UserRepository repository;

    @Override
    public void insert(User user) {
        user.encrypt(AES_USER_SECRET);
        repository.insert(user);
        user.decrypt(AES_USER_SECRET);
    }

    @Override
    public void updateByPassword(User user) {
        repository.updateByPassword(user);
    }

    @Override
    public void updateByProfile(User user) {
        user.encrypt(AES_USER_SECRET);
        repository.updateByProfile(user);
        user.decrypt(AES_USER_SECRET);
    }

    @Override
    public void updateByWithdrawnAt(User user) {
        repository.updateByWithdrawnAt(user);
    }

    @Override
    public User selectOneByIdAndWithdrawnAtIsNull(UserId userId) {
        return repository.selectOneByIdAndWithdrawnAtIsNull(userId)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Users selectAllByEmailAndWithdrawnAtIsNull(String email) {
        String encryptedEmail = AESEncryptor.getInstance().encrypt(email, AES_USER_SECRET);
        List<User> userList = repository.selectAllByEmailAndWithdrawnAtIsNull(encryptedEmail);
        return Users.of(userList);
    }

    @Override
    public Users selectAllByMobileAndWithdrawnAtIsNull(String mobile) {
        String encryptedMobile = AESEncryptor.getInstance().encrypt(mobile, AES_USER_SECRET);
        List<User> userList = repository.selectAllByMobileAndWithdrawnAtIsNull(encryptedMobile);
        return Users.of(userList);
    }
}
