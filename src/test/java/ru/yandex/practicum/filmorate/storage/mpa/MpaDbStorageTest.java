package ru.yandex.practicum.filmorate.storage.mpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(MpaDbStorage.class)
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaStorage;

    @Test
    void shouldGetAllMpa() {
        List<Mpa> mpaList = mpaStorage.getAllMpa();

        assertThat(mpaList).hasSize(5);
        assertThat(mpaList.get(0)).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    void shouldGetMpaById() {
        Optional<Mpa> mpa = mpaStorage.getMpaById(1);

        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(m ->
                        assertThat(m).hasFieldOrPropertyWithValue("name", "G"));
    }
}