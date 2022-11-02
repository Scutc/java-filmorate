package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Запрошено создание фильма " + film);

        if (validationFilm(film)) {
            filmStorage.createFilm(film);
            log.info("Фильм дробавлен");
        } else {
            log.warn("Фильм не добавлен!");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрошено обновление фильма " + film);
        if (filmStorage.getFilm(film.getId()) == null) {
            log.warn("Фильм c ID " + film.getId() + " не найден. Данные не обновлены");
            throw new FilmNotFoundException("Фильм не найден. Данные не обновлены!");
        }
        if (validationFilm(film)) {
            filmStorage.updateFilm(film);
            log.info("Фильм обновлен");
        } else {
            log.warn("Фильм не обновлен!");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Запрошен список всех фильмов");
        return filmStorage.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable long filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film != null) {
            return filmStorage.getFilm(filmId);
        } else {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
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
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);

        log.info("Запрошено добавление лайка для фильма " + filmId + " от пользователя " + userId);
        if (film == null) {
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден!");
        }
        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        filmService.addLike(filmId, userId);
        log.info("Лайк добавлен");
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable long filmId, @PathVariable long userId) {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);

        log.info("Запрошено удаление лайка для фильма " + filmId + " от пользователя " + userId);
        if (film == null) {
            log.warn("Фильм c ID " + filmId + " не найден!");
            throw new FilmNotFoundException("Фильм c ID " + filmId + " не найден!");
        }
        if (user == null) {
            log.warn("Пользователь с ID " + userId + " не найден");
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        filmService.removeLike(filmId, userId);
        log.info("Лайк удален");
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) int count) {
        log.info("Запрошен список фильмов с наибольшим количеством лайков. " + "Ограничитель количества фильмов равен "
                + count);
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
