package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);
    User updateUser(User user);
    User getUser(Long userId);
    void deleteUser(User user);
    List<User> getUsers();



}
