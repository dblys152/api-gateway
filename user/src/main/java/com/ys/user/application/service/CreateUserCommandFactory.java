package com.ys.user.application.service;

import com.ys.infrastructure.exception.BadRequestException;
import com.ys.infrastructure.utils.CommandFactory;
import com.ys.user.application.port.in.model.SignUpUserRequest;
import com.ys.user.domain.Account;
import com.ys.user.domain.CreateUserCommand;
import com.ys.user.domain.Profile;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class CreateUserCommandFactory implements CommandFactory<SignUpUserRequest, CreateUserCommand> {
    @Override
    public CreateUserCommand create(SignUpUserRequest request) {
        try {
            return new CreateUserCommand(
                    Account.create(request.getEmail(), request.getPassword()),
                    Profile.of(
                            request.getName(),
                            request.getMobile(),
                            request.getBirthDate().format(DateTimeFormatter.ISO_DATE),
                            request.getGender().name()
                    )
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
}
