package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {

        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.getUserById(user.getId()) == null) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", user.getId()));
        }
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {

        return userStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь или друг не найдены");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь или друг не найдены");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);
        if (user == null || otherUser == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}