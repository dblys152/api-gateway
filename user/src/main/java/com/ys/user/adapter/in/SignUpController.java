package com.ys.user.adapter.in;

import com.ys.infrastructure.exception.UnauthorizedException;
import com.ys.infrastructure.utils.ApiResponseModel;
import com.ys.infrastructure.utils.CommandFactory;
import com.ys.user.adapter.in.model.UserModel;
import com.ys.user.application.port.in.SignUpUseCase;
import com.ys.user.application.port.in.model.SignUpUserRequest;
import com.ys.user.domain.CreateUserCommand;
import com.ys.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/sign-up",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class SignUpController {
    @Value("${api-key.value.user}")
    private String USER_API_KEY;

    private final SignUpUseCase signUpUseCase;
    private final CommandFactory<SignUpUserRequest, CreateUserCommand> createUserCommandFactory;

    @PostMapping("")
    public ResponseEntity<ApiResponseModel<UserModel>> signUp(
            @RequestHeader("${api-key.header}") String apiKey,
            @RequestBody @Valid SignUpUserRequest request) {
        if (!USER_API_KEY.equals(apiKey)) {
            throw new UnauthorizedException("유효한 REST API 키가 아닙니다.");
        }

        User user = signUpUseCase.signUp(createUserCommandFactory.create(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseModel.success(HttpStatus.CREATED.value(), UserModel.fromDomain(user)));
    }
}
