package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class FilmStorageTest {

    FilmStorage filmStorage;

    @BeforeEach
    void filmTestsInitialization() {
        filmStorage = new InMemoryFilmStorage();
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
                         .build();

        filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getFilm(1L).id);

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
                         .build();
        filmStorage.createFilm(film1);

        Film filmUpdated = Film.builder()
                               .id(1L)
                               .name("Фильм 1")
                               .description("Обновленный фильм")
                               .releaseDate(LocalDate.of(2020, 10, 10))
                               .duration(100L)
                               .likes(new HashSet<>())
                               .build();

        filmStorage.updateFilm(filmUpdated);
        assertEquals("Обновленный фильм", filmStorage.getFilm(1L).description);
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
                         .build();

        filmStorage.createFilm(film1);

        Film film2 = Film.builder()
                         .id(2L)
                         .name("Фильм 2")
                         .description("Описание 2")
                         .releaseDate(LocalDate.of(2022, 10, 10))
                         .duration(90L)
                         .likes(new HashSet<>())
                         .build();
        filmStorage.createFilm(film2);
        assertEquals(2, filmStorage.getFilms().size());
    }

}
