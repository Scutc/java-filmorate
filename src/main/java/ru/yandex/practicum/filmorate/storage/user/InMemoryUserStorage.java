package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{

    private Map<Long, User> users;
    private int idGenerator;

    public InMemoryUserStorage() {
        users = new HashMap<>();
        idGenerator = 0;
    }

    @Override
    public User createUser(User user) {
        user.setId(++idGenerator);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь " + user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Обновлены данные пользователя " + user.getId());
        return null;
    }

    @Override
    public void deleteUser(User user) {
    }

    @Override
    public User getUser(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getUsers() {
        log.info("Запрошен список пользователей: " + users);
        return new ArrayList<>(users.values());
    }
}
