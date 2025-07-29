package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.CustomValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    User user1;
    User user2;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
        user1 = new User(1, "", "", "", LocalDate.of(2222, 10, 10),
                null);
        user2 = new User(0, "mail@mail.com", "Nagibator228", "Robert",
                LocalDate.of(1989, 9, 13), null);
    }

    @Test
    void getUsers_returnsList() {
        userController.addUser(user2);
        User newUser = user2;

        assertEquals(newUser, userController.getUsers().get(0));
    }

    @Test
    void addUser_fillsMap() {
        userController.addUser(user2);

        assertEquals(1, userController.getUsers().size());
    }

    @Test
    void updateUser_fillsMap() {
        userController.addUser(user2);
        User newUser = new User(1212, "", "", "", LocalDate.now(), null);
        newUser.setId(1);
        newUser.setEmail("mmm@mmm.mm");
        newUser.setLogin("Rocky");
        newUser.setName("Stanislav");
        newUser.setBirthday(LocalDate.of(1995, 5, 7));

        userController.updateUser(newUser);

        assertEquals(newUser, userController.getUsers().get(0));
    }

    @Test
    void addInvalidUser_throwsException() {
        Executable executable = () -> userController.addUser(user1);

        assertThrows(CustomValidationException.class, executable);
    }

    @Test
    void updateInvalidUser_throwsException() {
        Executable executable = () -> userController.updateUser(user1);

        assertThrows(CustomValidationException.class, executable);
    }
}
