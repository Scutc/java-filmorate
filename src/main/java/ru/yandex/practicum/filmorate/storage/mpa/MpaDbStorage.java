package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Mpa getMpaById(int mpaId) {
        String sql = "SELECT * FROM film_category WHERE category_id = ?";
        List<Mpa> mpas = jdbcTemplate.query(sql, (rs, rowNum) -> mapMpaFromRow(rs), mpaId);

        if (mpas.size() != 0) {
            Mpa mpa = mpas.get(0);
            log.info("Найден MPA-рейтинг: {} {}", mpa.getId(), mpa.getName());
            return mpa;
        } else {
            log.info("MPA-рейтинг с ID {} не найден.", mpaId);
            return null;
        }
    }

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM film_category";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapMpaFromRow(rs));
    }

    private Mpa mapMpaFromRow(ResultSet rs) throws SQLException {
        return Mpa.builder()
                  .id(rs.getInt("category_id"))
                  .name(rs.getString("name"))
                  .build();
    }

}
