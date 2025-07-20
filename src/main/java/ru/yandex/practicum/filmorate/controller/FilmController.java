package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> addFilm(@Valid @RequestBody Film film) {
        try {
            Film createdFilm = filmService.addFilm(film);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFilm(@Valid @RequestBody Film film) {
        try {
            Film updatedFilm = filmService.updateFilm(film);
            return ResponseEntity.ok(updatedFilm);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        return ResponseEntity.ok(filmService.getAllFilms());
    }

//    @PutMapping("/{id}/like/{userId}")
//    public ResponseEntity<?> addLike(@PathVariable int id, @PathVariable int userId) {
//        try {
//            filmService.addLike(id, userId);
//            return ResponseEntity.ok().build();
//        } catch (NotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
//        }
//    }
//    @DeleteMapping("/{id}/like/{userId}")
//    public ResponseEntity<?> removeLike(@PathVariable int id, @PathVariable int userId) {
//        try {
//            filmService.removeLike(id, userId);
//            return ResponseEntity.noContent().build();
//        } catch (NotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла непредвиденная ошибка на сервере"));
//        }
//    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(filmService.getPopularFilms(count));
    }
}
