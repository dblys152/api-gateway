package com.ys.user.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class Users {
    @Valid
    @NotNull
    List<User> items;

    public boolean isEmpty() {
        return this.items == null || this.items.isEmpty();
    }

    public void throwEmailDuplicationExceptionIfOtherUsersExist() {
        if (!isEmpty()) {
            throw new EmailDuplicationException();
        }
    }

    public void throwMobileDuplicationExceptionIfOtherUsersExist() {
        if (!isEmpty()) {
            throw new MobileDuplicationException();
        }
    }

    public void throwMobileDuplicationExceptionIfOtherUsersExistThanMe(UserId userId) {
        if (hasOtherUsersThanMe(userId)) {
            throw new MobileDuplicationException();
        }
    }

    private boolean hasOtherUsersThanMe(UserId userId) {
        return this.items.stream()
                .filter(i -> i.getUserId() != userId)
                .count() > 0;
    }
}
