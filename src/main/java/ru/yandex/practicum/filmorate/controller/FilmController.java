package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = (Logger) LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        try {
            Film createdFilm = filmService.addFilm(film);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);  // 201 Created
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();  // 400 Bad Request
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        try {
            Film updatedFilm = filmService.updateFilm(film);
            return ResponseEntity.ok(updatedFilm);  // 200 OK
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();  // 404 если фильм не найден
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> films = filmService.getAllFilms();
        if (films.isEmpty()) {
            return ResponseEntity.noContent().build();  // 204 если нет фильмов
        }
        return ResponseEntity.ok(films);  // 200 OK
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable int id, @PathVariable int userId) {
        try {
            filmService.addLike(id, userId);
            return ResponseEntity.ok().build();  // 200 OK
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();  // 404 если фильм/пользователь не найден
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<?> removeLike(@PathVariable int id, @PathVariable int userId) {
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.noContent().build();  // 204 No Content
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();  // 404 если фильм/пользователь не найден
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        try {
            List<Film> popularFilms = filmService.getPopularFilms(count);
            return ResponseEntity.ok(popularFilms);  // 200 OK
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();  // 404 если нет фильмов
        }
    }
}
