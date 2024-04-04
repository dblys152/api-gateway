package com.ys.user.application.service;


import com.ys.shared.exception.BadRequestException;
import com.ys.shared.utils.CommandFactory;
import com.ys.user.application.port.in.model.ChangeUserProfileRequest;
import com.ys.user.domain.ChangeUserProfileCommand;
import com.ys.user.domain.Profile;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ChangeUserProfileCommandFactory implements CommandFactory<ChangeUserProfileRequest, ChangeUserProfileCommand> {
    @Override
    public ChangeUserProfileCommand create(ChangeUserProfileRequest request) {
        try {
            return new ChangeUserProfileCommand(
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
