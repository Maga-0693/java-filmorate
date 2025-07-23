package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(UserDbStorage.class)
@RequiredArgsConstructor
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private User user;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        user = new User(0, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), Collections.emptyList());
    }

    @Test
    public void testFindUserById() {
        userStorage.addUser(user);

        User savedUser = userStorage.getUserById(1).orElseThrow();

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user);
    }

    @Test
    public void testFindAll() {
        userStorage.addUser(user);

        List<User> savedUsers = userStorage.getUsers();

        assertThat(savedUsers).hasSize(1);
    }

    @Test
    public void testCreate() {
        User newUser = userStorage.addUser(user);

        assertThat(newUser.getId()).isEqualTo(1);
    }

    @Test
    public void testUpdate() {
        userStorage.addUser(user);
        user.setName("No name");
        user.setId(1);

        userStorage.updateUser(user);

        assertThat(userStorage.getUserById(1).orElseThrow().getName())
                .isEqualTo("No name");
    }
}