package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.CustomValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.api.LikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private JdbcTemplate jdbcTemplate;

    Film film;
    Film film2;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        LikeStorage likeStorage = new LikeDbStorage(jdbcTemplate);
        UserStorage userStorage = new InMemoryUserStorage();

        GenreDbStorage genreStorage = new GenreDbStorage(jdbcTemplate);
        MpaDbStorage mpaStorage = new MpaDbStorage(jdbcTemplate);
        GenreService genreService = new GenreService(genreStorage);
        MpaService mpaService = new MpaService(mpaStorage);

        FilmService filmService = new FilmService(filmStorage, likeStorage, userStorage, genreService, mpaService);
        filmController = new FilmController(filmService);
        film = new Film(22, "", "", LocalDate.of(1777, 9, 2),
                -1, null, null, null);
        film2 = new Film(1, "Марко Поло", "Комедия про похождения друзей",
                LocalDate.of(2000, 10, 8), 135, null, Collections.emptyList(), new Mpa(1, "G"));
    }

    @Test
    void givenRightFilm_whenAddFilm_thenMapFilled() {
        filmController.addFilm(film2);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void givenRightFilm_whenGetFilms_thenGetListOfFilms() {
        filmController.addFilm(film2);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void givenWrongFilm_whenPostRequest_thenThrowException() {
        Executable executable = () -> filmController.addFilm(film);

        assertThrows(CustomValidationException.class, executable);
    }

    @Test
    void givenWrongFilm_whenPutRequest_thenThrowException() {
        Executable executable = () -> filmController.updateFilm(film);

        assertThrows(CustomValidationException.class, executable);
    }

    @Test
    void givenRightFilm_whenUpdateFilm_thenMapValueUpdated() {
        filmController.addFilm(film2);

        Film newFilm = new Film(222, "", "", LocalDate.now(), 222,
                null, null, null);
        newFilm.setId(film2.getId());
        newFilm.setName("Друзья");
        newFilm.setDescription("Ромком в классическом стиле");
        newFilm.setReleaseDate(LocalDate.of(1998, 7, 5));
        newFilm.setDuration(160);

        filmController.updateFilm(newFilm);

        assertEquals(filmController.getFilms().get(0), newFilm);
    }

    @Test
    void givenFilmWithNonExistentGenre_whenAddFilm_thenThrowNotFoundException() {
        Film film = new Film(1, "Film with non-existent genre", "Description",
                LocalDate.now(), 120, 0,
                Collections.singletonList(new Genre(999, "Non-existent Genre")),
                new Mpa(1, "G"));

        assertThrows(NotFoundException.class, () -> filmController.addFilm(film));
    }

    @Test
    void givenFilmWithNonExistentMpa_whenAddFilm_thenThrowNotFoundException() {
        Film film = new Film(1, "Film with non-existent MPA", "Description",
                LocalDate.now(), 120, 0,
                Collections.emptyList(),
                new Mpa(999, "Non-existent MPA"));

        assertThrows(NotFoundException.class, () -> filmController.addFilm(film));
    }

    @Test
    void givenNonExistentFilmId_whenGetFriends_thenThrowNotFoundException() {
        Integer nonExistentFilmId = 999;
        Executable executable = () -> filmController.getFriendsByFilmId(nonExistentFilmId);
        assertThrows(NotFoundException.class, executable);
    }
}