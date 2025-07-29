package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.debug("POST Запрос на создание нового пользователя");
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("PUT запрос на обновление пользователя");
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.debug("GET запрос на получение всех пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.debug("GET запрос на получение пользователя по указанному id= {}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(value = "id") Integer id, @PathVariable(value = "friendId") Integer friendId) {
        log.debug("PUT запрос на добавление дружеских отношений пользователя по id= {} и друга по id= {}", id, friendId);
        userService.getUserById(id);
        userService.getUserById(friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable(value = "id") Integer id, @PathVariable(value = "friendId") Integer friendId) {
        log.debug("DELETE запрос на удаление из списка друзей от указанного пользователя " +
                "id= {} и друга по id= {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> searchForUserFriends(@PathVariable Integer id) {
        log.debug("GET запрос на получение списка друзей пользователя по указанному id= {} ", id);
        userService.getUserById(id);
        return userService.searchForUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> searchForSameFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.debug("GET запрос на поиск общих друзей, если они существуют");
        return userService.searchForSameFriends(id, otherId);
    }
}