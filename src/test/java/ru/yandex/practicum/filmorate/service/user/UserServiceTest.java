package ru.yandex.practicum.filmorate.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserServiceTest {

    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    void userStorageTestsInitialization() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);

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

        User user3 = User.builder()
                         .id(3L)
                         .birthday(LocalDate.of(1995, 10, 10))
                         .email("user3@gmail.com")
                         .login("user3")
                         .name("Пользователь 3")
                         .friends(new HashSet<>())
                         .build();
        userStorage.createUser(user3);
    }

    @Test
    void addGetDeleteFriendTest() {
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 3L);
        assertEquals(2, userService.getUserFriends(1L).size());

        userService.removeFriend(1L, 3L);
        assertEquals(1, userService.getUserFriends(1L).size());
        assertFalse(userService.getUserFriends(1L).contains(3L));
    }

    @Test
    void getUsersCommonFriends() {
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 3L);
        userService.addFriend(3L, 2L);

        assertEquals(2L, userService.getUsersCommonFriends(1L, 3L).get(0).getId());
    }


}
