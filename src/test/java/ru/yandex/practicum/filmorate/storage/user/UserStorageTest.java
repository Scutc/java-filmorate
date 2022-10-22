package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserStorageTest {
    private UserStorage userStorage;

    @BeforeEach
    void userStorageTestsInitialization() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void userCreateGetUpdateDelete() {
        User user1 = User.builder()
                         .id(1L)
                         .birthday(LocalDate.of(2000, 10, 10))
                         .email("user1@gmail.com")
                         .login("user1")
                         .name("Пользователь 1")
                         .friends(new HashSet<>())
                         .build();

        userStorage.createUser(user1);
        assertEquals(1, userStorage.getUsers().size());

        userStorage.deleteUser(user1);
        assertNull(userStorage.getUser(1L));
    }

    @Test
    void updateUser() {
        User user1 = User.builder()
                         .id(1L)
                         .birthday(LocalDate.of(2000, 10, 10))
                         .email("user1@gmail.com")
                         .login("user1")
                         .name("Пользователь 1")
                         .friends(new HashSet<>())
                         .build();

        userStorage.createUser(user1);

        User userUpdated = User.builder()
                               .id(1L)
                               .birthday(LocalDate.of(2000, 10, 10))
                               .email("user1@gmail.com")
                               .login("user1")
                               .name("Пользователь Обновленный")
                               .friends(new HashSet<>())
                               .build();

        userStorage.updateUser(userUpdated);
        assertEquals("Пользователь Обновленный", userStorage.getUser(1L).getName());
    }

    @Test
    void getUsers() {
        User user1 = User.builder()
                         .id(1L)
                         .birthday(LocalDate.of(2000, 10, 10))
                         .email("user1@gmail.com")
                         .login("user1")
                         .name("Пользователь 1")
                         .friends(new HashSet<>())
                         .build();
        userStorage.createUser(user1);

        User user2 = User.builder()
                         .id(2L)
                         .birthday(LocalDate.of(1990, 10, 10))
                         .email("user2@gmail.com")
                         .login("user2")
                         .name("Пользователь 2")
                         .friends(new HashSet<>())
                         .build();
        userStorage.createUser(user2);

        assertEquals(2, userStorage.getUsers().size());
    }
}
