package com.ys.authentication.adapter.in;

import com.ys.authentication.adapter.in.model.TokenModel;
import com.ys.authentication.application.port.in.LoginUseCase;
import com.ys.authentication.application.port.in.model.SignInRequest;
import com.ys.authentication.domain.TokenInfo;
import com.ys.infrastructure.exception.UnauthorizedException;
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
@RequestMapping(value = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthenticationController {
    @Value("${api-key.value.user}")
    private String USER_API_KEY;
    @Value("${jwt.secret}")
    private String SECRET;

    private final LoginUseCase loginUseCase;

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponseModel<TokenModel>> login(
            @RequestHeader("${api-key.header}") String apiKey,
            @RequestBody @Valid SignInRequest request,
            HttpServletRequest httpServletRequest) {
        if (!USER_API_KEY.equals(apiKey)) {
            throw new UnauthorizedException("유효한 REST API 키가 아닙니다.");
        }
        String clientIp = IpFinder.getInstance().find(httpServletRequest);

        TokenInfo tokenInfo = loginUseCase.login(request.getEmail(), request.getPassword(), SECRET, clientIp);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponseModel.success(HttpStatus.OK.value(), TokenModel.fromDomain(tokenInfo)));
    }
}
