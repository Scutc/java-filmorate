package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM USERS_FRIENDS");
        jdbcTemplate.update("DELETE FROM USERS_FILMS");
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("DELETE FROM FILMS_GENRES");
        jdbcTemplate.update("DELETE FROM FILMS");
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

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUser(1L));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );

        user1.setName("After Update");
        userStorage.updateUser(user1);

        userOptional = Optional.ofNullable(userStorage.getUser(1L));
        assertThat(userOptional).isPresent()
                                .hasValueSatisfying(user -> assertThat(user)
                                        .hasFieldOrPropertyWithValue("name", "After Update"));

        userStorage.deleteUser(user1);
        Optional<User> optionalUser = Optional.ofNullable(userStorage.getUser(1L));
        assertThat(optionalUser).isEmpty();
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
