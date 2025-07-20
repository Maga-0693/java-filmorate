package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class, LikeDbStorage.class, GenreDbStorage.class, FilmGenreDbStorage.class})
class LikeDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private LikeDbStorage likeStorage;

    @Test
    void shouldAddAndRemoveLike() {
        Mpa mpa = new Mpa(1);

        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);

        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Film createdFilm = filmStorage.addFilm(film);
        User createdUser = userStorage.createUser(user);

        likeStorage.addLike(createdFilm.getId(), createdUser.getId());
        List<Integer> likes = likeStorage.getLikesByFilmId(createdFilm.getId());
        assertThat(likes).containsExactly(createdUser.getId());

        likeStorage.removeLike(createdFilm.getId(), createdUser.getId());
        likes = likeStorage.getLikesByFilmId(createdFilm.getId());
        assertThat(likes).isEmpty();
    }
}