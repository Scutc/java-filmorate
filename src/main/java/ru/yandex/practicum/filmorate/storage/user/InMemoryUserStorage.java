package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long idGenerator = 0L;

    @Override
    public User createUser(User user) {
        userCleaningUp(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        userCleaningUp(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean deleteUser(User user) {
        User removedUser = users.remove(user.getId());
        return removedUser != null;
    }

    @Override
    public User getUser(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
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
