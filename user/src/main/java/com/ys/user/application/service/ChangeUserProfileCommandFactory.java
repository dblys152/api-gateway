package com.ys.user.application.service;


import com.ys.infrastructure.exception.BadRequestException;
import com.ys.infrastructure.utils.CommandFactory;
import com.ys.user.application.port.in.model.ChangeUserProfileRequest;
import com.ys.user.domain.ChangeUserProfileCommand;
import com.ys.user.domain.Profile;
import org.springframework.stereotype.Component;

@Component
public class ChangeUserProfileCommandFactory implements CommandFactory<ChangeUserProfileRequest, ChangeUserProfileCommand> {
    @Override
    public ChangeUserProfileCommand create(ChangeUserProfileRequest request) {
        try {
            return new ChangeUserProfileCommand(
                    Profile.of(request.getName(), request.getMobile(), request.getBirthDate(), request.getGender()));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
}