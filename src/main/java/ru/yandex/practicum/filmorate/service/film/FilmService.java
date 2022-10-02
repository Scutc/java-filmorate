package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Long.compare;

@Service
public class FilmService {
    private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(long filmId, long userId) {
        filmStorage.getFilm(filmId).getLikes().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.getFilm(filmId).getLikes().remove(userId);
    }

    public List<Film> getTopFilms() {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt(f -> f.getLikes().size()))
                .limit(10)
                .collect(Collectors.toList());
    }

}
