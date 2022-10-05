package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        log.info("Запрошено создание пользователя " + user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (validationUser(user)) {
            userStorage.createUser(user);
            log.info("Добавлен пользователь " + user);
        } else {
            log.warn("Пользователь не добавлен");
            throw new ValidationException("Проверьте корректность введенных данных","POST/users");
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрошено обновление пользователя" + user);
        if (userStorage.getUser(user.getId()) == null) {
            log.warn("Пользователь с ID " + user.getId() + " не найден. Данные не обновлены!");
            throw new UserNotFoundException("Пользователь с ID " + user.getId()
                    + " не найден. Данные не обновлены!", "PUT/users");
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (userStorage.getUser(user.getId()) != null && validationUser(user)) {
            userStorage.updateUser(user);
            log.info("Данные пользователя обновлены " + user);
        } else {
            log.warn("Данные пользователя не обновлены, т.к. не пройдена валидация " + user);
            throw new ValidationException("Проверьте корректность введенных данных","PUT/users");
        }
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Запрошен список всех пользователей");
        return userStorage.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable long userId) {
        log.info("Запрошен пользователь с ID" + userId);
        User user = userStorage.getUser(userId);
        if (user != null) {
            log.info("Пользователь найден " + user);
            return userStorage.getUser(userId);
        } else {
            log.warn("Пользователь с ID " + userId + " не найден");
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден","GET/users/"+userId);
        }
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable long userId, @PathVariable long otherUserId) {
        log.info("Запрошен список общих друзей у пользователей " + userId + " и " + otherUserId);
        User user = userStorage.getUser(userId);
        User otherUser = userStorage.getUser(otherUserId);

        if (user == null || otherUser == null) {
            StringBuilder response = new StringBuilder();

            if (user == null) {
                log.warn("Пользователь с ID " + userId + " не найден. ");
                response.append("Пользователь с ID ").append(userId).append(" не найден. ");
            }
            if (otherUser == null) {
                log.warn("Пользователь с ID " + otherUserId + " не найден. ");
                response.append("Пользователь с ID ").append(otherUserId).append("не найден. ");
            }
            throw new UserNotFoundException(response.toString(), "GET/users/"
                    + userId + "/friends/common/" + otherUserId);
        }
        return userService.getUsersCommonFriends(userId, otherUserId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable long userId, @PathVariable long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        log.info("Пользователем " + userId + " запрошено добавление в друзья пользователя " + friendId );

        if (user == null || friend == null) {
            StringBuilder response = new StringBuilder();

            if (user == null) {
                log.warn("Пользователь с ID " + userId + " не найден. ");
                response.append("Пользователь с ID " + userId + " не найден. ");
            }
            if (friend == null) {
                log.warn(("Друг с ID " + friendId + " не найден. "));
                response.append("Друг с ID " + friendId + " не найден. ");
            }
            throw new UserNotFoundException(response.toString(), "PUT/users/" + userId + "/friends/" + friendId);
        }
        userService.addFriend(userId, friendId);
        log.info("Пользователь " + userId + " добавил в друзья " + friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        log.info("Пользователем " + userId + " запрошено удаление из друзей пользователя " + friendId);

        if (user == null || friend == null) {
            StringBuilder response = new StringBuilder();

            if (user == null) {
                log.warn("Пользователь с ID " + userId + "не найден. ");
                response.append("Пользователь с ID " + userId + "не найден. ");
            }
            if (friend == null) {
                log.warn("Друг с ID " + friendId + " не найден. ");
                response.append("Друг с ID " + friendId + " не найден. ");
            }
            throw new UserNotFoundException(response.toString(), "DELETE/users/" + userId + "/friends/" + friendId);
        }
        userService.deleteFriend(userId, friendId);
        log.info("Пользователь " + userId + " удалил из друзей " + friendId);

    }

    @GetMapping("/{userId}/friends")
    public List<User> getUserFriends(@PathVariable long userId) {
        log.info("Запрошен список друзей у пользователя " + userId);
        if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден","GET/users/"
                    + userId + "/friends");
        }
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
