package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);
    Film updateFilm(Film film);
    boolean deleteFilm(Film film);
    List<Film> getFilms();



}
