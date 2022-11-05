package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Qualifier("filmDbService")
public class FilmDbService implements FilmService{
    private final JdbcTemplate jdbcTemplate;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Override
    public void addLike(long filmId, long userId) {
        String sqlQuery = "INSERT INTO users_films (film_id, user_id, last_update)" +
                " VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId, LocalDateTime.now());
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sqlQuery = "DELETE FROM users_films WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int topCount) {
        String sqlQuery = "SELECT film_id, count (DISTINCT user_id) AS cnt " +
                "FROM users_films GROUP BY film_id ORDER BY cnt DESC LIMIT ?";
        List<Long> filmRows = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("film_id"), topCount);
        return filmStorage.getFilms(filmRows);
    }
}
