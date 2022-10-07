package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.HashSet;


public class FilmStorageTest {

    FilmStorage filmStorage = new InMemoryFilmStorage();

    @Test
    void createFilmTest() {

        Film film1 = new Film();
        Film film = new Film(1, "Фильм 1", "Описание",
                LocalDate.of(2020, 10, 10), 100L, new HashSet<>());

    }

    @Test
    void updateFilmTest() {

    }

    @Test
    void getFilmTest() {

    }

    @Test
    void deleteFilmTest() {

    }

    @Test
    void getFilmsTest() {

    }

}
