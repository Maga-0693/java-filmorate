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
        log.debug("GET request received to receive all films");
        return filmService.getFilms();
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.debug("POST request received to create new film");
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.addFilm(film));
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("PUT request received to update film by given entity");
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean like(@PathVariable(value = "id") Integer id, @PathVariable(value = "userId") Integer userId) {
        log.debug("PUT request received to add new like to film with id= {} by user with id= {}", id, userId);
        return filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unLike(@PathVariable(value = "id") Integer id, @PathVariable(value = "userId") Integer userId) {
        log.debug("PUT request received to add new like to film with id= {} by user with id= {}", id, userId);
        filmService.unlike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopCountOr10Films(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.debug("GET request received to get top 10 films");
        return filmService.getTopCountOr10Films(count);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(value = "id") Integer id) {
        log.debug("GET request received to get film by given id= {}", id);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFilmById(@PathVariable(value = "id") Integer id) {
        log.debug("DELETE request received to delete film by given id= {}", id);
        filmService.deleteFilmById(id);
        return ResponseEntity.ok("Film with ID " + id + " has been successfully deleted");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsByFilmId(@PathVariable(value = "id") Integer id) {
        log.debug("GET request received to get friends by film id= {}", id);
        return filmService.getFriendsByFilmId(id);
    }
}