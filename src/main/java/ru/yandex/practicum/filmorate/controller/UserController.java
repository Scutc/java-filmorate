package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (validationUser(user)) {
            userStorage.createUser(user);
        } else {
            log.warn("Пользователь не добавлен");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (userStorage.getUser(user.getId()) == null) {
            throw new UserNotFoundException("Пользователь с таким ID не найден. Данные не обновлены!");
        }
        if (userStorage.getUser(user.getId()) != null && validationUser(user)) {
            userStorage.updateUser(user);
        } else {
            log.warn("Данные пользователя не обновлены");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable long userId) {
        User user = userStorage.getUser(userId);
        if (user != null) {
            return userStorage.getUser(userId);
        } else {
            throw new UserNotFoundException("Пользователь с таким ID не найден");
        }
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable long userId, @PathVariable long otherUserId) {
        return userService.getUsersCommonFriends(userId, otherUserId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable long userId, @PathVariable long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getUserFriends(@PathVariable long userId) {
        return userService.getUserFriends(userId);
    }

    private boolean validationUser(User user) {
        boolean isValidated = true;

        if (user == null) {
            log.warn("Объект Пользователь не задан!");
            return false;
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            isValidated = false;
            log.warn("логин некорректный");
        }
        if (user.getEmail() == null) {
            isValidated = false;
            log.warn("email не задан");
        }
        if (!user.getEmail().matches(".*@.*")) {
            isValidated = false;
            log.warn("неверный формат email: " + user.getEmail());
        }
        if (user.getName() == null) {
            isValidated = false;
            log.warn("не задано имя пользователя");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            isValidated = false;
            log.warn("дата рождения в будущем: " + user.getBirthday());
        }
        return isValidated;
    }
}
