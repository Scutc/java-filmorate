package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {

    private final GenreStorage genreStorage;

    @Test
    public void getGenreById() {
        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.getGenreById(3));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(mpa -> assertThat(mpa).hasFieldOrPropertyWithValue("name", "Мультфильм"));
    }

    @Test
    public void getAllGenres() {
        List<Genre> mpas = genreStorage.getAllGenres();
        assertEquals(6, mpas.size());
    }
}
