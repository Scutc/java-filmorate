package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private Long idGenerator;

    //  Аналогично UsareStorage - пришлось отойти от заветов Lombok чтобы вычислить максимальный айдишник фильма
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
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
            for (Genre genre : genres) {
                insertFilmGenres(film.getId(), genre.getId());
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
        if (film.getGenres() != null) {
            Set<Genre> genreSorted = new TreeSet<>(Comparator.comparingInt(Genre::getId));
            genreSorted.addAll(film.getGenres());
            film.setGenres(genreSorted);
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
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilmFromRow(rs), filmId);
        if (films.size() > 0) {
            Film film = films.get(0);
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
            return mpaDbStorage.getMpaById(mpaSet.getInt("category_id"));
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
        TreeSet<Genre> genresTree = new TreeSet<>(Comparator.comparingInt(Genre::getId));
        genresTree.addAll(genres);
        return genresTree;
    }

    private Genre mapGenreFromRow(ResultSet rs) throws SQLException {
        return genreDbStorage.getGenreById(rs.getInt("genre_id"));
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
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapFilmFromRow(rs));
    }

    @Override
    public List<Film> getFilms(List<Long> filmsId) {
        String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT * FROM films WHERE film_id IN (%s)", inSql),
                (rs, rowNum) -> mapFilmFromRow(rs),
                filmsId.toArray());
    }


    private Film mapFilmFromRow(ResultSet rs) throws SQLException {
        return Film.builder()
                   .id(rs.getLong("film_id"))
                   .name(rs.getString("name"))
                   .description(rs.getString("description"))
                   .releaseDate(rs.getDate("release_date").toLocalDate())
                   .duration(rs.getLong("duration"))
                   .mpa(getMpa(rs.getLong("film_id")))
                   .likes(getLikes(rs.getLong("film_id")))
                   .genres(getGenres(rs.getLong("film_id")))
                   .build();
    }
}
