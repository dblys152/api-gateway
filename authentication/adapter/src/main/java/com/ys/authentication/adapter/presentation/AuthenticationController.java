package com.ys.authentication.adapter.presentation;

import com.ys.authentication.adapter.aspect.ClientIp;
import com.ys.authentication.adapter.presentation.model.TokenModel;
import com.ys.authentication.adapter.presentation.model.TokenPayloadModel;
import com.ys.authentication.application.usecase.GetTokenPayloadInfoUseCase;
import com.ys.authentication.application.usecase.LoginUseCase;
import com.ys.authentication.application.usecase.LogoutUseCase;
import com.ys.authentication.application.usecase.RefreshTokenUseCase;
import com.ys.authentication.application.usecase.model.LoginRequest;
import com.ys.authentication.application.usecase.model.RefreshTokenRequest;
import com.ys.authentication.domain.core.TokenInfo;
import com.ys.shared.exception.UnauthorizedException;
import com.ys.shared.jwt.PayloadInfo;
import com.ys.shared.utils.ApiResponseModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
    public Mono<ResponseEntity<ApiResponseModel<TokenModel>>> login(
            @RequestHeader("${api-key.header}") String apiKey,
            @ClientIp String clientIp,
            @RequestBody @Valid LoginRequest request,
            ServerWebExchange serverWebExchange) {
        if (!USER_API_KEY.equals(apiKey)) {
            return Mono.error(new UnauthorizedException("유효한 REST API 키가 아닙니다."));
        }

        Mono<TokenInfo> tokenInfoMono = loginUseCase.login(request.getEmail(), request.getPassword(), SECRET, clientIp);

        return tokenInfoMono
                .map(tokenInfo -> ResponseEntity.status(HttpStatus.OK).body(
                        ApiResponseModel.success(HttpStatus.OK.value(), TokenModel.fromDomain(tokenInfo))));
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponseModel<TokenModel>>> refresh(
            @RequestHeader("${api-key.header}") String apiKey,
            @RequestBody @Valid RefreshTokenRequest request) {
        if (!USER_API_KEY.equals(apiKey)) {
            throw new UnauthorizedException("유효한 REST API 키가 아닙니다.");
        }

        Mono<TokenInfo> tokenInfoMono = refreshTokenUseCase.refresh(request.getRefreshToken(), SECRET);

        return tokenInfoMono
                .map(tokenInfo -> ResponseEntity.status(HttpStatus.CREATED).body(
                        ApiResponseModel.success(HttpStatus.OK.value(), TokenModel.fromDomain(tokenInfo))));
    }

    @GetMapping(value = "/token-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponseModel<TokenPayloadModel>>> getTokenPayloadInfo() {
        Mono<PayloadInfo> payloadInfoMono = getTokenPayloadInfoUseCase.get();

        return payloadInfoMono
                .map(payloadInfo -> ResponseEntity.status(HttpStatus.OK).body(
                        ApiResponseModel.success(HttpStatus.OK.value(), TokenPayloadModel.from(payloadInfo))));
    }

    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponseModel<TokenModel>>> logout() {
        Mono<Void> resultMono = logoutUseCase.logout();

        return resultMono
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body(null)));
    }
}
