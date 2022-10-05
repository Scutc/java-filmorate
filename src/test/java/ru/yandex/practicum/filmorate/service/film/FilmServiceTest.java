package ru.yandex.practicum.filmorate.service.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;


public class FilmServiceTest {

    private FilmStorage filmStorage;

    @BeforeEach
    void testInitialization() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    public void addLikeTest() {
    }

    @Test
    public void removeLikeTest() {

    }


    @Test
    public void getTopFilmsTest() {

    }
}
