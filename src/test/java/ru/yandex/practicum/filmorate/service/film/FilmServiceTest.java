package ru.yandex.practicum.filmorate.service.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


public class FilmServiceTest {

    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;

    @BeforeEach
    void testInitialization() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
        userStorage = new InMemoryUserStorage();

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

        User user1 = User.builder()
                         .id(1L)
                         .birthday(LocalDate.of(2000, 10, 10))
                         .email("user1@gmail.com")
                         .login("user1")
                         .name("Пользователь 1")
                         .friends(new HashSet<>())
                         .build();
        userStorage.createUser(user1);

        User user2 = User.builder()
                         .id(2L)
                         .birthday(LocalDate.of(1990, 10, 10))
                         .email("user2@gmail.com")
                         .login("user2")
                         .name("Пользователь 2")
                         .friends(new HashSet<>())
                         .build();
        userStorage.createUser(user2);
    }

    @Test
    public void addAndRemoveLikeTest() {
        filmService.addLike(1L, 2L);
        filmService.addLike(1L, 1L);
        assertTrue(filmStorage.getFilm(1L).getLikes().contains(2L));

        filmService.removeLike(1L, 2L);
        assertFalse(filmStorage.getFilm(1L).getLikes().contains(2L));
    }

    @Test
    public void getTopFilmsTest() {
        filmService.addLike(1L, 2L);
        filmService.addLike(1L, 1L);
        filmService.addLike(2L, 1L);
        assertEquals(1L, filmService.getTopFilms(1).get(0).id);
    }
}
