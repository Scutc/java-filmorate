package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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

    Map<Integer, Film> films = new HashMap<>();
    int idGenerator = 0;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {

        if (filmValidation(film)) {
            film.setId(++idGenerator);
            films.put(film.getId(), film);
            log.info("Добавлен фильм " + film.getName());
        } else {
            log.warn("Фильм не добавлен!");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId()) && filmValidation(film)) {
            films.put(film.getId(), film);
            log.info("Обновлены данные фильма " + film.getName());
        } else {
            log.warn("Фильм не обновлен!");
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Запрошен список фильмов: " + films);
        return new ArrayList<>(films.values());
    }

    private boolean filmValidation(Film film) {
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
