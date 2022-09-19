package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
public class User {
    @NonNull
    private int id;
    @Email
    String email;
    @NonNull
    String login;
    String name;
    LocalDate birthday;
}
