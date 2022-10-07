package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;

@Slf4j
@Component

public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films = new HashMap<>();
    private Long idGenerator = 0L;

    @Override
    public Film createFilm(Film film) {
        filmCleaningUp(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmCleaningUp(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean deleteFilm(Film film) {
        Film removedFilm = films.remove(film.getId());
        if (removedFilm != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Long filmId) {
        return films.get(filmId);
    }

    private Film filmCleaningUp(final Film film) {
        if (film.getId() == null || film.getId() == 0L) {
            film.setId(++idGenerator);
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return film;
    }
}
