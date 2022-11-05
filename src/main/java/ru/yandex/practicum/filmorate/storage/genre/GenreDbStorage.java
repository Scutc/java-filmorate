package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public Genre getGenreById(int genreId) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> mapGenreFromRow(rs), genreId);

        if (genres.size() != 0) {
            Genre genre = genres.get(0);
            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());
            return genre;
        } else {
            log.info(" Жанр с ID {} не найден.", genreId);
            return null;
        }
    }

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapGenreFromRow(rs));
    }

    private Genre mapGenreFromRow(ResultSet rs) throws SQLException {
        return Genre.builder()
                    .id(rs.getInt("genre_id"))
                    .name(rs.getString("name"))
                    .build();
    }
}
