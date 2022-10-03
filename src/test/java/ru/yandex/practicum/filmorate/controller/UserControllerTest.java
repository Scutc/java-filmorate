package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

public class UserControllerTest {

    private UserStorage userStorage;
    private UserService userService;
    private UserController userController;

    @BeforeEach
    void modelInitialization() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userStorage, userService);
    }

    @Test
    void validationUserTestWithGoodData() throws ValidationException {
//  Создание пользователя с корректными данными
        User user1 = User.builder()
                .id(1)
                .login("user")
                .name("Dmitry")
                .email("user@yandex.ru")
                .birthday(LocalDate.of(1990, 12, 15))
                .build();

        boolean isValidated;
        userController.createUser(user1);
        isValidated = true;
        assertTrue(isValidated);
    }

    @Test
    void validationUserTestWithIncorrectEmail() throws ValidationException {
        boolean isValidated;
//  Создание пользователя с некорректным email
        User user2 = User.builder()
                .id(2)
                .login("user")
                .name("Dmitry")
                .email("")
                .birthday(LocalDate.of(1990, 12, 15))
                .build();
        try {
            userController.createUser(user2);
            isValidated = true;
        } catch (ValidationException e) {
            isValidated = false;
        }
        assertFalse(isValidated);

        User user3 = User.builder()
                .id(3)
                .login("user")
                .name("Dmitry")
                .email("dddd.yandex.ru")
                .birthday(LocalDate.of(1990, 12, 15))
                .build();
        try {
            userController.createUser(user3);
            isValidated = true;
        } catch (ValidationException e) {
            isValidated = false;
        }
        assertFalse(isValidated);
    }

    @Test
    void validationUserTestWithIncorrectLogin() throws ValidationException {
        boolean isValidated;
//  Создание пользователя с некорректным логином
        User user4 = User.builder()
                .id(4)
                .login("")
                .name("Dmitry")
                .email("user@yandex.ru")
                .birthday(LocalDate.of(1990, 12, 15))
                .build();
        try {
            userController.createUser(user4);
            isValidated = true;
        } catch (ValidationException e) {
            isValidated = false;
        }
        assertFalse(isValidated);

        User user5 = User.builder()
                .id(5)
                .login("use r")
                .name("Dmitry")
                .email("user@yandex.ru")
                .birthday(LocalDate.of(1990, 12, 15))
                .build();
        try {
            userController.createUser(user5);
            isValidated = true;
        } catch (ValidationException e) {
            isValidated = false;
        }
        assertFalse(isValidated);
    }

    @Test
    void validationUserTestWithIncorrectUserName() throws ValidationException {
        boolean isValidated;
//  Создание пользователя с некорректным именем
        User user6 = User.builder()
                .id(6)
                .login("user")
                .name("")
                .email("user@yandex.ru")
                .birthday(LocalDate.of(1990, 12, 15))
                .build();
        try {
            userController.createUser(user6);
            isValidated = true;
        } catch (ValidationException e) {
            isValidated = false;
        }
        assertTrue(isValidated);
    }

    @Test
    void validationUserTestWithIncorrectDateOfBirth() throws ValidationException {
        boolean isValidated;
//  Создание пользователя с некорректной датой рождения
        User user7 = User.builder()
                .id(7)
                .login("user")
                .name("Dmitry")
                .email("user@yandex.ru")
                .birthday(LocalDate.now().plusDays(1))
                .build();
        try {
            userController.createUser(user7);
            isValidated = true;
        } catch (ValidationException e) {
            isValidated = false;
        }
        assertFalse(isValidated);
    }
}
