package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private List<Film> films = new ArrayList<>();
    private int currentId = 1;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Фильм добавлен: {}", film.getName());
        film.setId(currentId++);
        films.add(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновление фильма с id: {}", film.getId());
        for (Film f : films) {
            if (f.getId() == film.getId()) {
                f.setName(film.getName());
                f.setDescription(film.getDescription());
                f.setReleaseDate(film.getReleaseDate());
                f.setDuration(film.getDuration());
                return f;
            }
        }
        throw new RuntimeException("Такой фильм не найден");
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }
}
