package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component("userDbStorage")

public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private Long idGenerator;

    /*Здесь пришлось вернуть конструктор несмотря на Lombok, т.к. пока не понял, как инициализировать переменную
    вспомогательным методом findMaxUserId при использовании аннотации @RequiredArgsConstructor. Получается,
    что на момент вызова метода определения максимальго ID объет jdbcTemplate еще не создан и происход ошибка
    NullPointer */
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.idGenerator = findMaxUserId();
    }

    private Long findMaxUserId () {
        String sqlQuery = "SELECT MAX(user_id) AS max_user_id FROM users";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery);
        if (userRows.next()) {
            return userRows.getLong("max_user_id");
        } else {
            return 0L;
        }
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO USERS (user_id, email, login, name, birthday, last_update)" +
                " VALUES (?, ?, ?, ?, ?, ?)";

        userCleaningUp(user);
        jdbcTemplate.update(sqlQuery, user.getId(), user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), LocalDateTime.now());
        return user;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User getUser(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, userId);

        if (userRows.next()) {
            User user = User.builder()
                            .id(userRows.getLong("user_id"))
                            .email(userRows.getString("email"))
                            .login(Objects.requireNonNull(userRows.getString("login")))
                            .name(userRows.getString("name"))
                            .birthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate())
                            .friends(getFriendsFromDb(userId))
                            .build();

            log.info("Найден пользователь: {} {}", user.getId(), user.getLogin());
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", userId);
            return null;
        }
    }

    private Set<Long> getFriendsFromDb(Long userId) {
        String sql = "SELECT * FROM USERS_FRIENDS WHERE USER_ID = ?";
        List<Long> friends = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id"));
        return new HashSet<>(friends);
    }


    @Override
    public boolean deleteUser(User user) {
        return false;
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    private User userCleaningUp (final User user) {
        if (user.getId() == null || user.getId() == 0L) {
            user.setId(++idGenerator);
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return user;
    }
}
