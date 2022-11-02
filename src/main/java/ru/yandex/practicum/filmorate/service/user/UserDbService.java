package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component("userDbService")
@RequiredArgsConstructor
public class UserDbService implements UserService {

    private final int FRIENDSHIP_APPROVED = 1;
    private final int FRIENDSHIP_NOT_APPROVED = 2;
    private final JdbcTemplate jdbcTemplate;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Override
    public User addFriend(long userId, long friendId) {
        String sqlQuery = "INSERT INTO USERS_FRIENDS (user_id, friend_id, friendship_status_id, last_update)" +
                " VALUES (?, ?, ?, ?)";

        List<User> friends = getUserFriends(userId);
        if (friends != null) {
            if (getUserFriends(userId).contains(friendId)) {
                jdbcTemplate.update(sqlQuery, userId, friendId, FRIENDSHIP_APPROVED, LocalDateTime.now());
            } else {
                jdbcTemplate.update(sqlQuery, userId, friendId, FRIENDSHIP_NOT_APPROVED, LocalDateTime.now());
            }
        } else {
            jdbcTemplate.update(sqlQuery, userId, friendId, FRIENDSHIP_NOT_APPROVED, LocalDateTime.now());
        }
        return userStorage.getUser(friendId);
    }

    @Override
    public User removeFriend(long userId, long friendId) {
        String sqlQuery = "DELETE FROM users_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        return userStorage.getUser(friendId);
    }

    @Override
    public List<User> getUserFriends(long userId) {
        String sqlQuery = "SELECT * FROM users_friends WHERE user_id = ?";
        List<Long> friends = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("friend_id"), userId);
        return friends.stream()
                      .map(userStorage::getUser)
                      .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersCommonFriends(long userId, long otherUserId) {
        String sqlQuery = "SELECT * FROM users_friends a INNER JOIN (SELECT * FROM users_friends WHERE user_id = ?) b ON a.friend_id = b.friend_id WHERE a.USER_ID = ?";
        List<Long> friends = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("friend_id"),
                userId, otherUserId);
        return friends.stream()
                      .map(userStorage::getUser)
                      .collect(Collectors.toList());
    }
}
