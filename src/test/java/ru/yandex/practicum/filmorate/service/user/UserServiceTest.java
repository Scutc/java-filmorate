package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("userDbService")
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void userStorageTestsInitialization() {
        jdbcTemplate.update("DELETE FROM USERS_FRIENDS");
        jdbcTemplate.update("DELETE FROM USERS_FILMS");
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("DELETE FROM FILMS_GENRES");
        jdbcTemplate.update("DELETE FROM FILMS");

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
        userService.addFriend(1L, 3L);
        assertEquals(2, userService.getUserFriends(1L).size());

        userService.removeFriend(1L, 3L);
        assertEquals(1, userService.getUserFriends(1L).size());
        assertFalse(userService.getUserFriends(1L).contains(3L));
    }

    @Test
    void getUsersCommonFriends() {
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 3L);
        userService.addFriend(3L, 2L);

        assertEquals(2L, userService.getUsersCommonFriends(1L, 3L).get(0).getId());
    }
}
