package com.ys.user.adapter.in;

import com.ys.infrastructure.utils.ApiResponseModel;
import com.ys.infrastructure.utils.CommandFactory;
import com.ys.user.adapter.in.model.UserModel;
import com.ys.user.application.port.in.ChangeUserPasswordUseCase;
import com.ys.user.application.port.in.ChangeUserProfileUseCase;
import com.ys.user.application.port.in.SignUpUseCase;
import com.ys.user.application.port.in.WithdrawUserUseCase;
import com.ys.user.application.port.in.model.ChangeUserPasswordRequest;
import com.ys.user.application.port.in.model.ChangeUserProfileRequest;
import com.ys.user.application.port.in.model.SignUpUserRequest;
import com.ys.user.domain.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/users",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class UserCommandController {
    private final SignUpUseCase signUpUseCase;
    private final CommandFactory<SignUpUserRequest, CreateUserCommand> createUserCommandFactory;
    private final ChangeUserProfileUseCase changeUserProfileUseCase;
    private final CommandFactory<ChangeUserProfileRequest, ChangeUserProfileCommand> changeUserProfileCommandFactory;
    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;
    private final WithdrawUserUseCase withdrawUserUseCase;

    @PostMapping("")
    public ResponseEntity<ApiResponseModel<UserModel>> signUp(@RequestBody @Valid SignUpUserRequest request) {
        User user = signUpUseCase.signUp(createUserCommandFactory.create(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseModel.success(HttpStatus.CREATED.value(), UserModel.fromDomain(user)));
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<ApiResponseModel<UserModel>> changeProfile(
            @PathVariable("userId") long userId,
            @RequestBody @Valid ChangeUserProfileRequest request) {
        User user = changeUserProfileUseCase.change(
                UserId.of(userId), changeUserProfileCommandFactory.create(request));

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponseModel.success(HttpStatus.OK.value(), UserModel.fromDomain(user)));
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable("userId") long userId,
            @RequestBody @Valid ChangeUserPasswordRequest request) {
        changeUserPasswordUseCase.change(UserId.of(userId), request.getPassword());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> withdraw(@PathVariable("userId") long userId) {
        withdrawUserUseCase.withdraw(UserId.of(userId));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
