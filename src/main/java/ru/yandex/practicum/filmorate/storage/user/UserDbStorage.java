package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private Long findMaxUserId() {
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
        deleteUser(user);
        return createUser(user);
    }

    @Override
    public User getUser(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapUserFromRow(rs), userId);
        if (users.size() > 0) {
            User user = users.get(0);
            log.info("Найден пользователь: {} {}", userId, user.getLogin());
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", userId);
            return null;
        }
    }

    private Set<Long> getFriendsFromDb(Long userId) {
        String sql = "SELECT * FROM USERS_FRIENDS WHERE USER_ID = ?";
        List<Long> friends = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id"), userId);
        return new HashSet<>(friends);
    }

    @Override
    public boolean deleteUser(User user) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        if (getUser(user.getId()) != null) {
            jdbcTemplate.update(sqlQuery, user.getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapUserFromRow(rs));
    }

    private User mapUserFromRow(ResultSet rs) throws SQLException {
        return User.builder()
                   .id(rs.getLong("user_id"))
                   .email(rs.getString("email"))
                   .login(Objects.requireNonNull(rs.getString("login")))
                   .name(rs.getString("name"))
                   .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                   .friends(getFriendsFromDb(rs.getLong("user_id")))
                   .build();
    }

    private User userCleaningUp(final User user) {
        if (user.getId() == null || user.getId() == 0L) {
            user.setId(++idGenerator);
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return user;
    }
}
