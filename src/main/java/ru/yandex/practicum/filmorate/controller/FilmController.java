package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        log.debug("GET запрос на получение всех фильмов");
        return filmService.getFilms();
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.debug("POST запрос на создание нового фильма");
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.addFilm(film));
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("PUT запрос на обновление фильма по заданному адресу");
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean like(@PathVariable(value = "id") Integer id, @PathVariable(value = "userId") Integer userId) {
        log.debug("PUT запрос на добавление нового лайка к фильму с id= {} от пользователя с id= {}", id, userId);
        return filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unLike(@PathVariable(value = "id") Integer id, @PathVariable(value = "userId") Integer userId) {
        log.debug("PUT запрос на добавление нового дизлайка к фильму с id= {} от пользователя с id= {}", id, userId);
        filmService.unlike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopCountOr10Films(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.debug("GET запрос на получение 10 лучших фильмов");
        return filmService.getTopCountOr10Films(count);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(value = "id") Integer id) {
        log.debug("GET запрос на получение фильма по указанному id= {}", id);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFilmById(@PathVariable(value = "id") Integer id) {
        log.debug("DELETE запрос на удаление фильма по указанному id= {}", id);
        filmService.deleteFilmById(id);
        return ResponseEntity.ok("Фильм с ID " + id + " был успешно удален");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsByFilmId(@PathVariable(value = "id") Integer id) {
        log.debug("GET запрос на получение друзей по id= {}", id);
        return filmService.getFriendsByFilmId(id);
    }
}