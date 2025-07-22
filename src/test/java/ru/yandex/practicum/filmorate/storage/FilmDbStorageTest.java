package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.api.*;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeDbStorage.class, UserDbStorage.class, MpaDbStorage.class})
public class FilmDbStorageTest {

    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    @MockBean
    private final LikeStorage likeStorage;
    @MockBean
    private final MpaStorage mpaStorage;
    private final UserStorage userStorage;
    private Film film;

    @BeforeEach
    void set() {
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);
        GenreService genreService = new GenreService(genreStorage);
        MpaService mpaService = new MpaService(mpaStorage);
        filmStorage = new FilmDbStorage(jdbcTemplate, genreStorage, likeStorage, mpaStorage);

        FilmService filmService = new FilmService(filmStorage, likeStorage, userStorage, genreService, mpaService);
        film = new Film(0, "Film1", "descFilm1", LocalDate.of(2023, 1, 1), 100, 1, Collections.emptyList(), new Mpa(1, "G"));
    }

    @Test
    @DirtiesContext
    void shouldAddFilm() {
        set();
        Film savedFilm = filmStorage.addFilm(film);

        assertThat(savedFilm).isNotNull().isEqualTo(film);
    }

    @Test
    @DirtiesContext
    void shouldCreateFilmWithId1() {
        set();
        Film savedFilm = filmStorage.addFilm(film);

        assertThat(savedFilm.getId()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void shouldUpdateFilm() {
        set();
        Film film = new Film(1, "Film1", "descFilm1", LocalDate.of(2023, 1, 1), 100, 1, Collections.emptyList(), new Mpa(1, "G"));
        filmStorage.addFilm(film);

        film.setName("Updated Film");
        film.setDescription("Updated description");
        film.setDuration(120);

        Film updatedFilm = filmStorage.updateFilm(film);

        assertThat(updatedFilm).isNotNull().isEqualTo(film);
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated description");
        assertThat(updatedFilm.getDuration()).isEqualTo(120);
    }
}