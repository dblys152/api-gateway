package com.ys.authentication.adapter.infrastructure.persistence;

import com.ys.user.domain.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Table(name = "USERS")
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class UserEntity {
    @Id
    private Long userId;

    @NotNull
    private UserType type;

    @NotNull
    private UserStatus status;

    @NotNull
    @Size(min = 8, max = 128)
    private String email;

    @NotNull
    @Size(min = 1, max = 255)
    private String password;

    @Size(min = 1, max = 255)
    private String oldPassword;

    private LocalDateTime passwordModifiedAt;

    private int passwordWrongCount;

    private LocalDateTime lastLoginAt;

    @NotNull
    @Size(min = 1, max = 128)
    private String name;

    @NotNull
    @Size(min = 1, max = 128)
    private String mobile;

    @Size(min = 1, max = 128)
    private String birthDate;

    @Size(min = 1, max = 128)
    private String gender;

    @NotNull
    private String roles;

    @NotNull
    private LocalDateTime joinedAt;

    @NotNull
    private LocalDateTime modifiedAt;

    private LocalDateTime withdrawnAt;

    public Mono<User> toDomain() {
        return Mono.just(User.of(
                UserId.of(this.userId),
                this.type,
                this.status,
                Account.of(
                        this.email,
                        this.password,
                        this.oldPassword,
                        this.passwordModifiedAt,
                        this.passwordWrongCount,
                        this.lastLoginAt
                ),
                Profile.of(
                        this.name,
                        this.mobile,
                        this.birthDate,
                        this.gender
                ),
                this.roles,
                this.joinedAt,
                this.modifiedAt,
                this.withdrawnAt
        ));
    }
}
