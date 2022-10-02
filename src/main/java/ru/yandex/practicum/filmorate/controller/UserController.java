package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        if (user.getName() == null) {
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
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (userStorage.getUsers().contains(user.getId()) && validationUser(user)) {
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
