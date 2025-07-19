package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FilmDbStorage.class, GenreDbStorage.class, FilmGenreDbStorage.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private GenreDbStorage genreStorage;

    @Autowired
    private FilmGenreDbStorage filmGenreStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpaId(new Mpa(1, "G", "General Audiences"))
                .build();
    }

    @Test
    void shouldCreateAndFindFilmById() {
        Film createdFilm = filmStorage.addFilm(testFilm);

        Optional<Film> foundFilm = Optional.ofNullable(filmStorage.getFilmById(createdFilm.getId()));

        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isPositive();
                    assertThat(film.getName()).isEqualTo("Test Film");
                    assertThat(film.getDuration()).isEqualTo(120);
                    assertThat(film.getMpaId().getId()).isEqualTo(1);
                });
    }

    @Test
    void shouldUpdateFilm() {
        Film createdFilm = filmStorage.addFilm(testFilm);

        createdFilm.setName("Updated Name");
        createdFilm.setDuration(150);

        Film updatedFilm = filmStorage.updateFilm(createdFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Name");
        assertThat(updatedFilm.getDuration()).isEqualTo(150);
    }

    @Test
    void shouldGetAllFilms() {
        filmStorage.addFilm(testFilm);
        filmStorage.addFilm(Film.builder()
                .name("Another Film")
                .description("Another Description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(90)
                .mpaId(new Mpa(2, "PG", "Parental Guidance"))
                .build());

        List<Film> films = filmStorage.getAllFilms();

        assertThat(films)
                .hasSize(2)
                .extracting(Film::getName)
                .containsExactly("Test Film", "Another Film");
    }

    @Test
    void shouldManageFilmGenres() {
        Film createdFilm = filmStorage.addFilm(testFilm);

        filmGenreStorage.addGenreToFilm(createdFilm.getId(), 1);
        filmGenreStorage.addGenreToFilm(createdFilm.getId(), 3);

        List<Integer> genreIds = filmGenreStorage.getFilmGenreIds(createdFilm.getId());
        assertThat(genreIds).containsExactly(1, 3);

        filmGenreStorage.updateFilmGenres(createdFilm.getId(), List.of(2, 5));

        List<Integer> updatedGenreIds = filmGenreStorage.getFilmGenreIds(createdFilm.getId());
        assertThat(updatedGenreIds).containsExactly(2, 5);
    }

    @Test
    void shouldReturnEmptyListWhenNoFilmsExist() {
        List<Film> films = filmStorage.getAllFilms();
        assertThat(films).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenFilmNotFound() {
        Optional<Film> foundFilm = Optional.ofNullable(filmStorage.getFilmById(999));
        assertThat(foundFilm).isEmpty();
    }
}
