package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private static final String FRIENDS_PATH = "/{id}/friends";
    private static final String FRIEND_PATH = "/{id}/friends/{friendId}";
    private static final String COMMON_FRIENDS_PATH = "/{id}/friends/common/{otherId}";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создан пользователь: {}", user.getLogin());
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновлен пользователь с id: {}", user.getId());
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }

    @PutMapping(FRIEND_PATH)
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {

        userService.addFriend(id, friendId);
    }

    @DeleteMapping(FRIEND_PATH)
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping(FRIENDS_PATH)
    public List<User> getFriends(@PathVariable int id) {

        return userService.getFriends(id);
    }

    @GetMapping(COMMON_FRIENDS_PATH)
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
