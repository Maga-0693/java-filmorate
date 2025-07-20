package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        getUserByIdOrThrow(user.getId());
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {

        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {

        return getUserByIdOrThrow(id);
    }

    @Transactional
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Пользователь не может добавить сам себя в друзья");
        }

        User user = getUserByIdOrThrow(userId);
        User friend = getUserByIdOrThrow(friendId);

        if (friendStorage.hasFriendshipRequest(friendId, userId)) {
            friendStorage.confirmFriendship(userId, friendId);
        } else {
            friendStorage.addFriend(userId, friendId, User.FriendshipStatus.UNCONFIRMED);
        }
    }

    private User getUserByIdWithFriends(int userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        // Загружаем информацию о друзьях
        Map<Integer, User.FriendshipStatus> friends = friendStorage.getFriendsWithStatus(userId);
        user.setFriends(new HashMap<>(friends));

        return user;
    }

    public void removeFriend(int userId, int friendId) {
        getUserByIdOrThrow(userId);
        getUserByIdOrThrow(friendId);
        friendStorage.removeFriend(userId, friendId);
    }

    public void confirmFriendship(int userId, int friendId) {
        getUserByIdOrThrow(userId);
        getUserByIdOrThrow(friendId);

        if (!friendStorage.hasFriendshipRequest(friendId, userId)) {
            throw new NotFoundException("Запрос на дружбу не найден");
        }

        friendStorage.confirmFriendship(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        getUserByIdOrThrow(userId);
        return friendStorage.getFriends(userId);
    }

    public List<User> getConfirmedFriends(int userId) {
        getUserByIdOrThrow(userId);
        List<Integer> friendIds = friendStorage.getConfirmedFriends(userId);
        List<User> friends = new ArrayList<>();
        for (Integer id : friendIds) {
            friends.add(getUserByIdOrThrow(id));
        }
        return friends;
    }

    public List<User> getFriendshipRequests(int userId) {
        getUserByIdOrThrow(userId);
        List<Integer> requesterIds = friendStorage.getFriendshipRequests(userId);
        List<User> requesters = new ArrayList<>();
        for (Integer id : requesterIds) {
            requesters.add(getUserByIdOrThrow(id));
        }
        return requesters;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        getUserByIdOrThrow(userId);
        getUserByIdOrThrow(otherId);

        Set<Integer> userFriends = new HashSet<>(friendStorage.getFriendsIds(userId));
        Set<Integer> otherFriends = new HashSet<>(friendStorage.getFriendsIds(otherId));
        userFriends.retainAll(otherFriends);

        List<User> commonFriends = new ArrayList<>();
        for (Integer id : userFriends) {
            commonFriends.add(getUserByIdOrThrow(id));
        }
        return commonFriends;
    }

    private User getUserByIdOrThrow(int userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}