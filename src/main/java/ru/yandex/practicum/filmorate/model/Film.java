package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.*;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата фильма не может быть пустой")
    @PastOrPresent(message = "Дата фильма не может быть еще не наступившей")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    private Set<Integer> likes = new HashSet<>();
}
