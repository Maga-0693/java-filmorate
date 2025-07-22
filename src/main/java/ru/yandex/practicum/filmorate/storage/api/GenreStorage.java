package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Optional<Genre> getById(Integer id);

    List<Genre> getAll();

    List<Genre> getGenresByFilmId(int filmId);
}