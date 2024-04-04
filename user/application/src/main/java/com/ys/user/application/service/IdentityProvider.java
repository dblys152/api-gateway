package com.ys.user.application.service;

import com.ys.shared.exception.UnauthorizedException;
import com.ys.shared.jwt.PayloadInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class IdentityProvider {
    public PayloadInfo getPayloadInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("인증된 사용자를 찾을 수 없습니다.");
        }

        return (PayloadInfo) authentication.getPrincipal();
    }
}
