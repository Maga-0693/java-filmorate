package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.List;

@Service
@Data
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {

        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        try {
            userStorage.addFriend(userId, friendId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public void removeFriend(Integer userId, Integer friendId) {
        try {
            userStorage.removeFriend(userId, friendId);
        } catch (NotFoundException e) {
            throw e;
        }
    }

    public List<User> searchForUserFriends(Integer id) {
        try {
            return userStorage.searchForUserFriends(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Не найден пользователь с id: " + id);
        }
    }

    public List<User> searchForSameFriends(Integer userId, Integer friendId) {
        return userStorage.searchForSameFriends(userId, friendId);
    }

    public User addUser(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {

        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {

        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        try {
            return userStorage.getUserById(id)
                    .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не существует"));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + id + " не существует");
        }
    }
}