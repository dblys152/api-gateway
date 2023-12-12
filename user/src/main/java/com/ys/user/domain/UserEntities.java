package com.ys.user.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class UserEntities {
    @Valid
    @NotNull
    List<UserEntity> items;

    public boolean isEmpty() {
        return this.items == null || this.items.isEmpty();
    }

    public void hasOtherUsersThenEmailDuplicationException() {
        if (!isEmpty()) {
            throw new EmailDuplicationException();
        }
    }

    public void hasOtherUsersThenMobileDuplicationException() {
        if (!isEmpty()) {
            throw new MobileDuplicationException();
        }
    }

    public void hasOtherUsersExceptMeThenMobileDuplicationException(UserId userId) {
        if (hasOtherUsersExcept(userId)) {
            throw new MobileDuplicationException();
        }
    }

    private boolean hasOtherUsersExcept(UserId userId) {
        return this.items.stream()
                .filter(i -> i.getUserId() != userId)
                .count() > 0;
    }
}
