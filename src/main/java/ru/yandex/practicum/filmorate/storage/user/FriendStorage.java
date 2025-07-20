package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.User.FriendshipStatus;
import java.util.List;
import java.util.Map;

public interface FriendStorage {
    void addFriend(int userId, int friendId, FriendshipStatus status);

    void removeFriend(int userId, int friendId);

    void confirmFriendship(int userId, int friendId);

    void updateFriendshipStatus(int userId, int friendId, FriendshipStatus status);

    boolean hasFriendshipRequest(int requesterId, int recipientId);

    List<Integer> getFriendsIds(int userId);

    Map<Integer, FriendshipStatus> getFriendsWithStatus(int userId);

    List<Integer> getFriendshipRequests(int userId);

    List<Integer> getConfirmedFriends(int userId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId1, int userId2);

}
