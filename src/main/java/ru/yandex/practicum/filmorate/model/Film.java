package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @PositiveOrZero
    private Long duration;
    Set<Long> likes;
}
