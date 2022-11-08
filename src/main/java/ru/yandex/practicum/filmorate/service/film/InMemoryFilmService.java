package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Qualifier("InMemoryFilmService")
public class InMemoryFilmService implements FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    public void addLike(long filmId, long userId) {
        filmStorage.getFilm(filmId).addLike(userId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.getFilm(filmId).removeLike(userId);
    }

    public List<Film> getTopFilms(int topCount) {
        return filmStorage.getFilms()
                          .stream()
                          .sorted((t1, t2) -> t2.getLikes().size() - t1.getLikes().size())
                          .limit(topCount)
                          .collect(Collectors.toList());
    }
}
