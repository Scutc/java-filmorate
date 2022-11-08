package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTest {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void filmTestsInitialization() {
        jdbcTemplate.update("DELETE FROM USERS_FRIENDS");
        jdbcTemplate.update("DELETE FROM USERS_FILMS");
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("DELETE FROM FILMS_GENRES");
        jdbcTemplate.update("DELETE FROM FILMS");
    }

    @Test
    void filmCreateGetDeleteFilmTest() {
        Film film1 = Film.builder()
                         .id(1L)
                         .name("Фильм 1")
                         .description("Описание")
                         .releaseDate(LocalDate.of(2020, 10, 10))
                         .duration(100L)
                         .likes(new HashSet<>())
                         .mpa(Mpa.builder().id(1).build())
                         .genres(new HashSet<>())
                         .build();

        filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getFilm(1L).getId());

        filmStorage.deleteFilm(film1);
        assertNull(filmStorage.getFilm(1L));
    }

    @Test
    void updateFilmTest() {
        Film film1 = Film.builder()
                         .id(1L)
                         .name("Фильм 1")
                         .description("Описание")
                         .releaseDate(LocalDate.of(2020, 10, 10))
                         .duration(100L)
                         .likes(new HashSet<>())
                         .mpa(Mpa.builder().id(1).build())
                         .genres(new HashSet<>())
                         .build();
        filmStorage.createFilm(film1);

        Film filmUpdated = Film.builder()
                               .id(1L)
                               .name("Фильм 1")
                               .description("Обновленный фильм")
                               .releaseDate(LocalDate.of(2020, 10, 10))
                               .duration(100L)
                               .likes(new HashSet<>())
                               .mpa(Mpa.builder().id(1).build())
                               .genres(new HashSet<>())
                               .build();

        filmStorage.updateFilm(filmUpdated);
        assertEquals("Обновленный фильм", filmStorage.getFilm(1L).getDescription());
    }

    @Test
    void getFilmsTest() {
        Film film1 = Film.builder()
                         .id(1L)
                         .name("Фильм 1")
                         .description("Описание")
                         .releaseDate(LocalDate.of(2020, 10, 10))
                         .duration(100L)
                         .likes(new HashSet<>())
                         .mpa(Mpa.builder().id(1).build())
                         .genres(new HashSet<>())
                         .build();

        filmStorage.createFilm(film1);

        Film film2 = Film.builder()
                         .id(2L)
                         .name("Фильм 2")
                         .description("Описание фильма 2")
                         .releaseDate(LocalDate.of(2020, 10, 10))
                         .duration(100L)
                         .likes(new HashSet<>())
                         .mpa(Mpa.builder().id(1).build())
                         .genres(new HashSet<>())
                         .build();
        filmStorage.createFilm(film2);
        assertEquals(2, filmStorage.getFilms().size());
    }

}
