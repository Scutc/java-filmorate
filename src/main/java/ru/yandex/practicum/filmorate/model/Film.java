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
    public long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    public String name;
    @Size(max = 200, message = "Превышена максимальная длина описания")
    public String description;
    public LocalDate releaseDate;
    @PositiveOrZero(message = "Продолжительность фильма не может быть отрицательной")
    public Long duration;
    private Set<Long> likes;
}
