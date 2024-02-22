package com.ys.authentication.adapter.in;

import com.ys.authentication.adapter.in.model.TokenModel;
import com.ys.authentication.adapter.in.model.TokenPayloadModel;
import com.ys.authentication.application.port.in.GetTokenPayloadInfoUseCase;
import com.ys.authentication.application.port.in.LoginUseCase;
import com.ys.authentication.application.port.in.LogoutUseCase;
import com.ys.authentication.application.port.in.RefreshTokenUseCase;
import com.ys.authentication.application.port.in.model.RefreshTokenRequest;
import com.ys.authentication.application.port.in.model.LoginRequest;
import com.ys.authentication.domain.TokenInfo;
import com.ys.infrastructure.exception.UnauthorizedException;
import com.ys.infrastructure.jwt.PayloadInfo;
import com.ys.infrastructure.utils.ApiResponseModel;
import com.ys.infrastructure.utils.IpFinder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class AuthenticationController {
    @Value("${api-key.value.user}")
    private String USER_API_KEY;
    @Value("${jwt.secret}")
    private String SECRET;

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final GetTokenPayloadInfoUseCase getTokenPayloadInfoUseCase;
    private final LogoutUseCase logoutUseCase;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseModel<TokenModel>> login(
            @RequestHeader("${api-key.header}") String apiKey,
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpServletRequest) {
        if (!USER_API_KEY.equals(apiKey)) {
            throw new UnauthorizedException("유효한 REST API 키가 아닙니다.");
        }
        String clientIp = IpFinder.getInstance().find(httpServletRequest);

        TokenInfo tokenInfo = loginUseCase.login(request.getEmail(), request.getPassword(), SECRET, clientIp);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponseModel.success(HttpStatus.OK.value(), TokenModel.fromDomain(tokenInfo)));
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseModel<TokenModel>> refresh(
            @RequestHeader("${api-key.header}") String apiKey,
            @RequestBody @Valid RefreshTokenRequest request) {
        if (!USER_API_KEY.equals(apiKey)) {
            throw new UnauthorizedException("유효한 REST API 키가 아닙니다.");
        }

        TokenInfo tokenInfo = refreshTokenUseCase.refresh(request.getRefreshToken(), SECRET);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseModel.success(HttpStatus.OK.value(), TokenModel.fromDomain(tokenInfo)));
    }

    @GetMapping(value = "/token-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseModel<TokenPayloadModel>> getTokenPayloadInfo() {
        PayloadInfo payloadInfo = getTokenPayloadInfoUseCase.get();

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponseModel.success(HttpStatus.OK.value(), TokenPayloadModel.from(payloadInfo)));
    }

    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseModel<TokenModel>> logout() {
        logoutUseCase.logout();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
