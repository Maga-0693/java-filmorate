package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.User.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FriendshipDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int userId, int friendId, FriendshipStatus status) {
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?) " +
                "ON CONFLICT (user_id, friend_id) DO UPDATE SET status = EXCLUDED.status";
        jdbcTemplate.update(sql, userId, friendId, status.toString());
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void confirmFriendship(int userId, int friendId) {
        // Специализированный метод для подтверждения дружбы
        updateFriendshipStatus(userId, friendId, FriendshipStatus.CONFIRMED);
        updateFriendshipStatus(friendId, userId, FriendshipStatus.CONFIRMED);
    }

    @Override
    public void updateFriendshipStatus(int userId, int friendId, FriendshipStatus status) {
        String sql = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
        int updated = jdbcTemplate.update(sql, status.toString(), userId, friendId);

        if (updated == 0) {
            throw new IllegalArgumentException("Friendship relation not found");
        }
    }

    // Остальные методы остаются без изменений
    @Override
    public boolean hasFriendshipRequest(int requesterId, int recipientId) {
        String sql = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ? AND status = 'UNCONFIRMED'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, requesterId, recipientId);
        return count != null && count > 0;
    }

    @Override
    public List<Integer> getFriendsIds(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }

    @Override
    public Map<Integer, FriendshipStatus> getFriendsWithStatus(int userId) {
        String sql = "SELECT friend_id, status FROM friends WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        new AbstractMap.SimpleEntry<>(
                                rs.getInt("friend_id"),
                                FriendshipStatus.valueOf(rs.getString("status"))
                        ), userId)
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public List<Integer> getFriendshipRequests(int userId) {
        String sql = "SELECT user_id FROM friends WHERE friend_id = ? AND status = 'UNCONFIRMED'";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }

    @Override
    public List<Integer> getConfirmedFriends(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ? AND status = 'CONFIRMED'";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = "SELECT u.* FROM users u JOIN friends f ON u.user_id = f.friend_id WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId);
    }

    @Override
    public List<User> getCommonFriends(int userId1, int userId2) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f1 ON u.user_id = f1.friend_id " +
                "JOIN friends f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId1, userId2);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                    .id(rs.getInt("user_id"))
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .name(rs.getString("user_name"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .build();
        }
    }
}
