package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата фильма не может быть пустой")
    @PastOrPresent(message = "Дата фильма не может быть еще не наступившей")
    @ReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
}
