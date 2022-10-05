package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;


public class FilmControllerTests {

    private FilmController filmController;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private FilmService filmService;
    private UserService userService;

    @BeforeEach
    void modelInitialization() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        filmService = new FilmService(filmStorage);
        filmController = new FilmController(filmStorage, filmService, userStorage);
    }

    @Test
    void validationFilmWithGoodData() throws ValidationException {
// Тест создания фильма, соответствующего критериям
        Film film1 = Film.builder()
                .id(1)
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(2022, 10, 15))
                .duration(90L)
                .build();

        boolean isValidated;
        try {
            filmController.createFilm(film1);
            isValidated = true;
        } catch (ValidationException e) {
            isValidated = false;
        }
        assertTrue(isValidated);
    }

    @Test
    void validationFilmWithLongDescription() throws ValidationException {
        boolean isValidated;
// Тест создания фильма с описанием  более 200 символов
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= 201; i++) {
            stringBuilder.append("f");
        }

        Film film2 = Film.builder()
                .id(2)
                .name("Фильм 2")
                .description(stringBuilder.toString())
                .releaseDate(LocalDate.of(2022, 10, 15))
                .duration(90L)
                .build();
        try {
            filmController.createFilm(film2);
            isValidated = true;
        } catch (Exception e) {
            isValidated = false;
        }
        assertFalse(isValidated);
    }

    @Test
    void validationFilmWithIncorrectDate() throws ValidationException {
        boolean isValidated;
//Создание фильма с некорректной датой
        Film film3 = Film.builder()
                .id(3)
                .name("Фильм 3")
                .description("Описание фильма 3")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(90L)
                .build();
        try {
            filmController.createFilm(film3);
            isValidated = true;
        } catch (Exception e) {
            isValidated = false;
        }
        assertFalse(isValidated);
    }

    @Test
    void validationFilmWithNegativeDuration() throws ValidationException {
        boolean isValidated;
        //Создание фильма с отрицательной продолжительностью
        Film film4 = Film.builder()
                .id(4)
                .name("Фильм 4")
                .description("Описание фильма 4")
                .releaseDate(LocalDate.of(2020, 12, 27))
                .duration(-1L)
                .build();
        try {
            filmController.createFilm(film4);
            isValidated = true;
        } catch (Exception e) {
            isValidated = false;
        }
        assertFalse(isValidated);
    }

    @Test
    void validationFilmWithEmptyName() throws ValidationException {
        boolean isValidated;
//Создание фильма c пустным названием
        Film film5 = Film.builder()
                .id(5)
                .name("")
                .description("Описание фильма 5")
                .releaseDate(LocalDate.of(2020, 12, 27))
                .duration(90L)
                .build();
        try {
            filmController.createFilm(film5);
            isValidated = true;
        } catch (Exception e) {
            isValidated = false;
        }
        assertFalse(isValidated);
    }
}

