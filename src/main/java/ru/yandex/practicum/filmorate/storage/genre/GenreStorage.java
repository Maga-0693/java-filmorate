package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

public interface GenreStorage {
    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    List<Genre> getFilmGenres(int filmId);

    void addFilmGenre(int filmId, int genreId);
    
    void removeFilmGenres(int filmId);
}
