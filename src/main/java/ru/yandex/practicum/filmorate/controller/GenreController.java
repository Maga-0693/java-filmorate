package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getGenreById() {
        log.debug("GET запрос на получение всех жанров");
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.debug("GET запрос получения жанра по id=" + id);
        return genreService.getGenreById(id);
    }
}