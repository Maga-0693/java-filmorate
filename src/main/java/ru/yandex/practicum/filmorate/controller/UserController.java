package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private List<User> users = new ArrayList<>();
    private int currentId = 1;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создан новый пользователь: {}", user.getLogin());
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(currentId++);
        users.add(user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновлен существующий пользователь приложения с id: {}", user.getId());
        for (User existingUser : users) {
            if (existingUser.getId() == user.getId()) {
                existingUser.setEmail(user.getEmail());
                existingUser.setLogin(user.getLogin());
                existingUser.setName(user.getName());
                existingUser.setBirthday(user.getBirthday());
                return existingUser;
            }
        }
        throw new RuntimeException("Существующий пользователь приложения не найден");
    }

    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }
}
