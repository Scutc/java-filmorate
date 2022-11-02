package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;
    private Long idGenerator;

//  Аналогично UsareStorage - пришлось отойти от заветов Lombok чтобы вычислить максимальный айдишник фильма
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        idGenerator = findMaxFilmId();
    }

    private Long findMaxFilmId() {
        String sqlQuery = "SELECT MAX(film_id) AS max_film_id FROM films";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery);
        if (userRows.next()) {
            return userRows.getLong("max_film_id");
        } else {
            return 0L;
        }
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

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO FILMS (film_id, name, description, release_date, duration, last_update, category_id)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";

        filmCleaningUp(film);
        jdbcTemplate.update(sqlQuery, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), LocalDateTime.now(), film.getMpa());

        film.getGenres().stream().peek(t -> insertfilmGenres(film.getId(), t));
        return film;
    }

    private void insertfilmGenres(Long filmId, Long genreId) {
        String sqlQuery = "INSERT INTO films_genres (film_id, genre_id, last_update)";
        jdbcTemplate.update(sqlQuery, filmId, genreId, LocalDateTime.now());
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Film getFilm(Long filmId) {
        return null;
    }

    @Override
    public boolean deleteFilm(Film film) {
        return false;
    }

    @Override
    public List<Film> getFilms() {
        return null;
    }
}
