package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;
    private Long idGenerator;

//  Аналогично UsareStorage - пришлось отойти от заветов Lombok чтобы вычислить максимальный айдишник фильма
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        idGenerator = findMaxFilmId();
    }


    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO FILMS (film_id, name, description, release_date, duration, last_update, category_id)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";

        filmCleaningUp(film);
        jdbcTemplate.update(sqlQuery, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), LocalDateTime.now(), film.getMpa().getId());
        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            Iterator<Genre> iterator = genres.iterator();
            while (iterator.hasNext()) {
                insertFilmGenres(film.getId(), iterator.next().getId());
            }
        }
        return film;
    }

    private void insertFilmGenres(Long filmId, Integer genreId) {
        String sqlQuery = "INSERT INTO films_genres (film_id, genre_id, last_update) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId, LocalDateTime.now());
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
    public Film updateFilm(Film film) {
        deleteFilm(film);
        return createFilm(film);
    }

    @Override
    public Film getFilm(Long filmId) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(sql, filmId);

        if (filmsRows.next()) {
            Film film = Film.builder()
                    .id(filmsRows.getLong("film_id"))
                    .name(filmsRows.getString("name"))
                    .description(filmsRows.getString("description"))
                    .releaseDate(filmsRows.getDate("release_date").toLocalDate())
                    .duration(filmsRows.getLong("duration"))
                    .mpa(getMpa(filmId))
                    .likes(getLikes(filmId))
                    .genres(getGenres(filmId))
                    .build();

            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info(" Фильм с идентификатором {} не найден.", filmId);
            return null;
        }
    }

    private Mpa getMpa(Long filmId) {
        String sql = "SELECT * FROM film_category WHERE " +
                "category_id IN (SELECT category_id FROM films WHERE film_id = ?)";
        SqlRowSet mpaSet = jdbcTemplate.queryForRowSet(sql, filmId);

        if (mpaSet.next()) {
            Mpa mpa = Mpa.builder()
                         .id(mpaSet.getInt("category_id"))
                         .name(mpaSet.getString("name"))
                         .build();
            return mpa;
        }
        return null;
    }

    private Set<Long> getLikes(Long filmId) {
        String sql = "SELECT * FROM users_films WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
        return new HashSet<>(likes);
    }

    private Set<Genre> getGenres(Long filmId) {
        String sql = "SELECT * FROM genres g INNER JOIN films_genres fg " +
                "ON g.genre_id = fg.GENRE_ID" +
                " WHERE film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> mapGenreFromRow(rs), filmId);
        return new HashSet<>(genres);
    }

    private Genre mapGenreFromRow(ResultSet rs) throws SQLException {
        Genre genre = Genre.builder()
                           .id(rs.getInt("genre_id"))
                           .name(rs.getString("name"))
                           .build();
        return genre;
    }

    @Override
    public boolean deleteFilm(Film film) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        if (getFilm(film.getId()) != null) {
            jdbcTemplate.update(sqlQuery, film.getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        List<Film> filmRows = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilmFromRow(rs));
        return filmRows;
    }

    private Film mapFilmFromRow(ResultSet rs) throws SQLException {
        Film film = Film.builder()
                        .id(rs.getLong("film_id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getLong("duration"))
                        .mpa(getMpa(rs.getLong("film_id")))
                        .likes(getLikes(rs.getLong("film_id")))
                        .genres(getGenres(rs.getLong("film_id")))
                        .build();
        return film;
    }
}
