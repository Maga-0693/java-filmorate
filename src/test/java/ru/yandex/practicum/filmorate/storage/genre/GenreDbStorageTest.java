package ru.yandex.practicum.filmorate.storage.genre;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(GenreDbStorage.class)
class GenreDbStorageTest {

    @Autowired
    private GenreDbStorage genreStorage;

    @Test
    void shouldGetAllGenres() {
        List<Genre> genres = genreStorage.getAllGenres();

        assertThat(genres).hasSize(6);
        assertThat(genres.get(0)).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    void shouldGetGenreById() {
        Optional<Genre> genre = Optional.ofNullable(genreStorage.getGenreById(1));

        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g).hasFieldOrPropertyWithValue("name", "Комедия"));
    }
}