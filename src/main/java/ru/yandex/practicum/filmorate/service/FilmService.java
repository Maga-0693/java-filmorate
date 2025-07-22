package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.storage.api.LikeStorage;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.*;

@Service
@Slf4j
@Data
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final GenreService genreService;
    private final MpaService mpaService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, LikeStorage likeStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage, GenreService genreService, MpaService mpaService) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
        this.genreService = genreService;
        this.mpaService = mpaService;
    }

    public boolean like(Integer filmId, Integer userId) {
        likeStorage.like(filmId, userId);
        return true;
    }

    public void unlike(Integer filmId, Integer userId) {
        if (userId < 1) {
            throw new NotFoundException("User not exist");
        }
        if (filmId < 1) {
            throw new NotFoundException("Film not exist");
        }
        getFilmById(filmId);
        userStorage.getUserById(userId);
        likeStorage.unLike(filmId, userId);
        log.debug("Удален лайк у фильма с  ID=" + filmId);
    }

    public List<Film> getTopCountOr10Films(Integer count) {

        return filmStorage.getMostNLikedFilms(count);
    }

    public List<Film> getFilms() {

        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        // Проверяем MPA
        if (film.getMpa() != null) {
            mpaService.getMpaById(film.getMpa().getId());
        }

        // Проверяем жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreService.getGenreById(genre.getId());
            }
        }

        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {

        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Integer id) {

        return filmStorage.getFilmById(id);
    }

    public void deleteFilmById(Integer id) {

        filmStorage.deleteFilmById(id);
    }

    public List<User> getFriendsByFilmId(Integer filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Film with id " + filmId + " does not exist.");
        }
        return getFriendsForFilm(filmId);
    }

    private List<User> getFriendsForFilm(Integer filmId) {
        return Collections.emptyList();
    }
}