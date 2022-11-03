package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.sql.In;

@Data
@Builder
public class Genre {
    private Integer id;
    private String name;
}
