package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Map;

@RestController
@RequestMapping("/films")
public class LikeController {

    private final FilmService filmService;

    @Autowired
    public LikeController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> addLike(
            @PathVariable int id,
            @PathVariable int userId
    ) {
        try {
            filmService.addLike(id, userId);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Произошла непредвиденная ошибка"));
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<?> removeLike(
            @PathVariable int id,
            @PathVariable int userId
    ) {
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Произошла непредвиденная ошибка"));
        }
    }
}