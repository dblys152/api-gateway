package com.ys.authentication.adapter.out;

import com.ys.authentication.adapter.out.persistence.AuthenticationInfoRepository;
import com.ys.authentication.application.port.out.RecordAuthenticationInfoPort;
import com.ys.authentication.domain.AuthenticationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationInfoPersistenceAdapter implements RecordAuthenticationInfoPort {
    private final AuthenticationInfoRepository repository;

    @Override
    public void insert(AuthenticationInfo authenticationInfo) {
        repository.insert(authenticationInfo);
    }
}
