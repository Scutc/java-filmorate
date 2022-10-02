package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {

        if (validationFilm(film)) {
            filmStorage.createFilm(film);
        } else {
            log.warn("Фильм не добавлен!");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (filmStorage.getFilms().contains(film) && validationFilm(film)) {
            filmStorage.updateFilm(film);
        } else {
            log.warn("Фильм не обновлен!");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @DeleteMapping
    public boolean deleteFilms(@Valid @RequestBody Film film) {
        if (filmStorage.getFilms().contains(film)) {
            return filmStorage.deleteFilm(film);
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
