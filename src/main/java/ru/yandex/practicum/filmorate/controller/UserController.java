package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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

    Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 0;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (userValidation(user)) {
            user.setId(++idGenerator);
            users.put(user.getId(), user);
            log.info("Добавлен пользователь " + user.getId());
        } else {
            log.warn("Пользователь не добавлен");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (users.containsKey(user.getId()) && userValidation(user)) {
            users.put(user.getId(), user);
            log.info("Обновлены данные пользователя " + user.getId());
        } else {
            log.warn("Данные пользователя не обновлены");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Запрошен список пользователей: " + users);
        return new ArrayList<>(users.values());
    }

    private boolean userValidation(User user) {
        boolean isValidated = true;

        if (user == null) {
            log.warn("Объект Пользователь не задан!");
            return false;
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
