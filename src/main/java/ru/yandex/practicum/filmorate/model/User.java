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
    private Long id;
    @Email(message = "Некорректный формат email")
    private String email;
    @NonNull
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends;

    public boolean addFriend(Long friendId) {
        return friends.add(friendId);
    }

    public boolean removeFriend(Long friendId) {
        return friends.remove(friendId);
    }
}
