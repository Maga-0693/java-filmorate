package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.User.FriendshipStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, FriendshipDbStorage.class})
class FriendshipDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private FriendshipDbStorage friendshipStorage;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        User tempUser1 = new User();
        tempUser1.setEmail("user1@example.com");
        tempUser1.setLogin("user1");
        tempUser1.setName("User One");
        tempUser1.setBirthday(LocalDate.of(1990, 1, 1));
        user1 = userStorage.createUser(tempUser1);

        User tempUser2 = new User();
        tempUser2.setEmail("user2@example.com");
        tempUser2.setLogin("user2");
        tempUser2.setName("User Two");
        tempUser2.setBirthday(LocalDate.of(1995, 1, 1));
        user2 = userStorage.createUser(tempUser2);

        User tempUser3 = new User();
        tempUser3.setEmail("user3@example.com");
        tempUser3.setLogin("user3");
        tempUser3.setName("User Three");
        tempUser3.setBirthday(LocalDate.of(2000, 1, 1));
        user3 = userStorage.createUser(tempUser3);
    }

    @Test
    void shouldAddAndRemoveFriend() {
        // Добавление друга
        friendshipStorage.addFriend(user1.getId(), user2.getId(), FriendshipStatus.UNCONFIRMED);

        // Проверка добавления
        Map<Integer, FriendshipStatus> friends = friendshipStorage.getFriendsWithStatus(user1.getId());
        assertThat(friends)
                .hasSize(1)
                .containsEntry(user2.getId(), FriendshipStatus.UNCONFIRMED);

        // Удаление друга
        friendshipStorage.removeFriend(user1.getId(), user2.getId());

        // Проверка удаления
        friends = friendshipStorage.getFriendsWithStatus(user1.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    void shouldConfirmFriendship() {
        // Отправка запроса на дружбу
        friendshipStorage.addFriend(user1.getId(), user2.getId(), FriendshipStatus.UNCONFIRMED);

        // Подтверждение дружбы
        friendshipStorage.confirmFriendship(user2.getId(), user1.getId());

        // Проверка статусов
        Map<Integer, FriendshipStatus> user1Friends = friendshipStorage.getFriendsWithStatus(user1.getId());
        Map<Integer, FriendshipStatus> user2Friends = friendshipStorage.getFriendsWithStatus(user2.getId());

        assertThat(user1Friends)
                .hasSize(1)
                .containsEntry(user2.getId(), FriendshipStatus.CONFIRMED);

        assertThat(user2Friends)
                .hasSize(1)
                .containsEntry(user1.getId(), FriendshipStatus.CONFIRMED);
    }

    @Test
    void shouldUpdateFriendshipStatus() {
        friendshipStorage.addFriend(user1.getId(), user2.getId(), FriendshipStatus.UNCONFIRMED);

        // Обновление статуса
        friendshipStorage.updateFriendshipStatus(
                user1.getId(),
                user2.getId(),
                FriendshipStatus.CONFIRMED);

        // Проверка обновления
        Map<Integer, FriendshipStatus> friends = friendshipStorage.getFriendsWithStatus(user1.getId());
        assertThat(friends)
                .hasSize(1)
                .containsEntry(user2.getId(), FriendshipStatus.CONFIRMED);
    }

    @Test
    void shouldGetFriendshipRequests() {
        friendshipStorage.addFriend(user1.getId(), user2.getId(), FriendshipStatus.UNCONFIRMED);
        friendshipStorage.addFriend(user1.getId(), user3.getId(), FriendshipStatus.UNCONFIRMED);

        List<Integer> requests = friendshipStorage.getFriendshipRequests(user2.getId());

        assertThat(requests)
                .hasSize(1)
                .containsExactly(user1.getId());
    }

    @Test
    void shouldGetConfirmedFriends() {
        friendshipStorage.addFriend(user1.getId(), user2.getId(), FriendshipStatus.CONFIRMED);
        friendshipStorage.addFriend(user1.getId(), user3.getId(), FriendshipStatus.UNCONFIRMED);

        List<Integer> confirmedFriends = friendshipStorage.getConfirmedFriends(user1.getId());

        assertThat(confirmedFriends)
                .hasSize(1)
                .containsExactly(user2.getId());
    }

    @Test
    void shouldGetCommonFriends() {
        // Создаем общего друга
        User commonFriend = new User();
        commonFriend.setEmail("common@example.com");
        commonFriend.setLogin("common");
        commonFriend.setName("Common Friend");
        commonFriend.setBirthday(LocalDate.of(1998, 5, 15));
        commonFriend = userStorage.createUser(commonFriend);

        // Добавляем друзей
        friendshipStorage.addFriend(user1.getId(), commonFriend.getId(), FriendshipStatus.CONFIRMED);
        friendshipStorage.addFriend(user2.getId(), commonFriend.getId(), FriendshipStatus.CONFIRMED);

        // Получаем общих друзей
        List<User> commonFriends = friendshipStorage.getCommonFriends(user1.getId(), user2.getId());

        // Проверяем результат
        assertThat(commonFriends)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(commonFriend.getId());
    }

    @Test
    void shouldCheckFriendshipRequest() {
        friendshipStorage.addFriend(user1.getId(), user2.getId(), FriendshipStatus.UNCONFIRMED);

        boolean hasRequest = friendshipStorage.hasFriendshipRequest(user1.getId(), user2.getId());

        assertTrue(hasRequest);
    }

    @Test
    void shouldGetFriends() {
        friendshipStorage.addFriend(user1.getId(), user2.getId(), FriendshipStatus.CONFIRMED);
        friendshipStorage.addFriend(user1.getId(), user3.getId(), FriendshipStatus.CONFIRMED);

        List<User> friends = friendshipStorage.getFriends(user1.getId());

        assertThat(friends)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(user2.getId(), user3.getId());
    }
}
