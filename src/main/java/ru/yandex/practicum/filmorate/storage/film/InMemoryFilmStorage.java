package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j

@Component

public class InMemoryFilmStorage implements FilmStorage{

    private Map<Long, Film> films = new HashMap<>();
    private int idGenerator = 0;

    @Override
    public Film createFilm(Film film) {
        film.setId(++idGenerator);
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean deleteFilm(Film film) {
        Film removedFilm =  films.remove(film.getId());
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
}
