package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private FilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {

        if (validationFilm(film)) {
            inMemoryFilmStorage.createFilm(film);
        } else {
            log.warn("Фильм не добавлен!");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (inMemoryFilmStorage.getFilms().contains(film) && validationFilm(film)) {
            inMemoryFilmStorage.updateFilm(film);
        } else {
            log.warn("Фильм не обновлен!");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    @DeleteMapping
    public boolean deleteFilms(@Valid @RequestBody Film film) {
        if (inMemoryFilmStorage.getFilms().contains(film)) {
            return inMemoryFilmStorage.deleteFilm(film);
        } else {
            return false;
        }
    }

    private boolean validationFilm(Film film) {
        boolean isValidated = true;

        if (film == null) {
            log.warn("Объект Фильм не задан!");
            return false;
        }

        if (film.getName().isBlank()) {
            isValidated = false;
            log.warn("Не задано название фильма");
        }
        if (film.getDescription().length() > 200) {
            isValidated = false;
            log.warn("Превышена длина описания фильма");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            isValidated = false;
            log.warn("Неверная дата выхода фильма: " + film.getReleaseDate());
        }
        if (film.getDuration() < 0) {
            isValidated = false;
            log.warn("Отрицательная продолжительность фильма: " + film.getDuration());
        }
        return isValidated;
    }
}
