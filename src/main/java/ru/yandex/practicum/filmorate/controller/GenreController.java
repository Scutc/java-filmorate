package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreDbStorage genreDbStorage;

    @GetMapping
    public List<Genre> getGenres() {
        log.info("Запрошен список всех жанров");
        return genreDbStorage.getAllGenres();
    }

    @GetMapping("/{genreId}")
    public Genre getGenre(@PathVariable int genreId) {
        log.info("Запрошен жанр с ID = {}", genreId);
        Genre genre = genreDbStorage.getGenreById(genreId);

        if (genre != null) {
            log.info("Найден жанр {} {}", genreId, genre.getName());
            return genre;
        } else {
            log.warn("Жанр с ID " + genreId + " не найден");
            throw new GenreNotFoundException("Жанр с ID " + genreId + " не найден");
        }
    }
}
