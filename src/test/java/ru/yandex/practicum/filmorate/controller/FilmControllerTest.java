package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private Validator validator;
    private Film film;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G", "General Audiences"));
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());
    }

    @Test
    void whenNameIsBlank_thenValidationFails() {
        film.setName(" ");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void whenDescriptionIsTooLong_thenValidationFails() {
        film.setDescription("a".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Максимальная длина описания — 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void whenReleaseDateIsTooEarly_thenValidationFails() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации");

        boolean hasDateError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate"));
        assertTrue(hasDateError, "Ожидалась ошибка валидации для поля releaseDate");
    }

    @Test
    void whenReleaseDateIsMinAllowed_thenValidationPasses() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenDurationIsNegative_thenValidationFails() {
        film.setDuration(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Продолжительность фильма должна быть положительным числом",
                violations.iterator().next().getMessage());
    }

    @Test
    void whenDurationIsZero_thenValidationFails() {
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenValidFilm_thenNoValidationErrors() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenMpaIsNull_thenValidationFails() {
        film.setMpa(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации при null MPA");

        boolean hasMpaError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("mpa"));
        assertTrue(hasMpaError, "Ожидалась ошибка валидации для поля mpa");
    }

    @Test
    void whenReleaseDateIsNull_thenValidationFails() {
        film.setReleaseDate(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenDescriptionIsNull_thenValidationPasses() {
        film.setDescription(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNameIsNull_thenValidationFails() {
        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}