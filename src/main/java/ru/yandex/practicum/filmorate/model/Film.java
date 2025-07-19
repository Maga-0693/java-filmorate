package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    private Mpa mpaId;
    private Set<Genre> genres;
    private Set<Integer> likes;

    // Конструктор по умолчанию
    public Film() {
        this.genres = new HashSet<>();
        this.likes = new HashSet<>();
    }

    // Полный конструктор
    public Film(int id, String name, String description, LocalDate releaseDate,
                int duration, Mpa mpa, Set<Genre> genres, Set<Integer> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaId = mpa;
        this.genres = genres != null ? genres : new HashSet<>();
        this.likes = likes != null ? likes : new HashSet<>();
    }
}
