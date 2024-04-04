package com.ys.authentication.adapter.infrastructure;

import com.ys.authentication.adapter.infrastructure.persistence.UserReactiveRepository;
import com.ys.authentication.domain.user.LoadUserPort;
import com.ys.shared.encryption.AESEncryptor;
import com.ys.shared.exception.ExceptionMessages;
import com.ys.user.domain.User;
import com.ys.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort {
    @Value("${aes.user-secret}")
    private String AES_USER_SECRET;

    private final UserReactiveRepository repository;

    @Override
    public Mono<User> findByIdAndWithdrawnAtIsNull(UserId userId) {
        return repository.findByIdAndWithdrawnAtIsNull(userId)
                .switchIfEmpty(Mono.error(new NoSuchElementException(ExceptionMessages.NO_DATA_MESSAGE)));
    }

    @Override
    public Mono<User> findByEmailAndWithdrawnAtIsNull(String email) {
        String encryptedEmail = AESEncryptor.getInstance().encrypt(email, AES_USER_SECRET);
        return repository.findByEmailAndWithdrawnAtIsNull(encryptedEmail)
                .switchIfEmpty(Mono.error(new NoSuchElementException(ExceptionMessages.NO_DATA_MESSAGE)));
    }
}
