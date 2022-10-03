package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {

        if (validationFilm(film)) {
            filmStorage.createFilm(film);
        } else {
            log.warn("Фильм не добавлен!");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (filmStorage.getFilm(film.getId()) == null) {
            log.warn("Фильм c id " + film.getId() + " не найден. Данные не обновлены");
            throw new FilmNotFoundException("Фильм не найден. Данные не обновлены!");
        }
        if (validationFilm(film)) {
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

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable long filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film != null) {
            return filmStorage.getFilm(filmId);
        } else {
            throw new FilmNotFoundException("Фильм с таким ID не найден");
        }
    }

    @DeleteMapping
    public boolean deleteFilms(@Valid @RequestBody Film film) {
        if (filmStorage.getFilms().contains(film)) {
            return filmStorage.deleteFilm(film);
        } else {
            return false;
        }
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(
            @RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.getTopFilms(count);
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
