package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        user = new User();
        user.setEmail("email@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void whenEmailIsBlank_thenValidationFails() {
        user.setEmail(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getMessage().contains("Электронная почта не может быть пустой") ||
                        v.getMessage().contains("Электронная почта должна содержать символ @")));
    }

    @Test
    void whenEmailHasNoAtSymbol_thenValidationFails() {
        user.setEmail("email.example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Электронная почта должна содержать символ @", violations.iterator().next().getMessage());
    }

    @Test
    void whenLoginIsBlank_thenValidationFails() {
        user.setLogin(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getMessage().contains("Логин не может быть пустым") ||
                        v.getMessage().contains("Логин не может содержать пробелы")));
    }

    @Test
    void whenLoginHasSpaces_thenValidationFails() {
        user.setLogin("log in");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void whenBirthdayIsInFuture_thenValidationFails() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void whenNameIsEmpty_thenLoginUsedAsName() {
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenValidUser_thenNoValidationErrors() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
}
