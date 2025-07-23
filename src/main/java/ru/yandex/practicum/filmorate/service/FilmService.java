package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.CustomValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.storage.api.LikeStorage;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.time.LocalDate;
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
            throw new NotFoundException("Пользователь не существует");
        }
        if (filmId < 1) {
            throw new NotFoundException("Фильм не существует");
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
        validateFilm(film);
        if (film.getMpa() != null) {
            mpaService.getMpaById(film.getMpa().getId());
        }

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
            throw new NotFoundException("Фильм с id " + filmId + " не существует");
        }
        return getFriendsForFilm(filmId);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new CustomValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new CustomValidationException("Описание фильма не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new CustomValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new CustomValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private List<User> getFriendsForFilm(Integer filmId) {

        return Collections.emptyList();
    }
}