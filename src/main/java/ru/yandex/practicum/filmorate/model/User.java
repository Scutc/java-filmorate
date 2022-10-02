package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private @NonNull long id;
    @Email
    String email;
    @NonNull
    String login;
    String name;
    LocalDate birthday;
    Set<Long> friends;
}
