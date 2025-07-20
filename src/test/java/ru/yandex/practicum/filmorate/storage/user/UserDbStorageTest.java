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
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserDbStorage.class, FriendshipDbStorage.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private FriendshipDbStorage friendshipStorage;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testLogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldCreateAndRetrieveUser() {
        User createdUser = userStorage.createUser(testUser);

        Optional<User> retrievedUser = userStorage.getUserById(createdUser.getId());

        assertThat(retrievedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isPositive();
                    assertThat(user.getEmail()).isEqualTo("test@example.com");
                    assertThat(user.getLogin()).isEqualTo("testLogin");
                });
    }

    @Test
    void shouldUpdateUser() {
        User createdUser = userStorage.createUser(testUser);
        createdUser.setName("Updated Name");

        User updatedUser = userStorage.updateUser(createdUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");

        Optional<User> retrievedUser = userStorage.getUserById(createdUser.getId());
        assertThat(retrievedUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getName()).isEqualTo("Updated Name")
                );
    }

    @Test
    void shouldGetAllUsers() {
        userStorage.createUser(testUser);
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setLogin("anotherLogin");
        anotherUser.setBirthday(LocalDate.of(1995, 5, 15));
        userStorage.createUser(anotherUser);

        List<User> users = userStorage.getAllUsers();

        assertThat(users)
                .hasSize(2)
                .extracting(User::getLogin)
                .containsExactly("testLogin", "anotherLogin");
    }

    @Test
    void shouldManageFriendships() {
        User user1 = userStorage.createUser(testUser);

        User user2 = new User();
        user2.setEmail("friend@example.com");
        user2.setLogin("friendLogin");
        user2.setBirthday(LocalDate.of(1992, 3, 10));
        user2 = userStorage.createUser(user2);

        friendshipStorage.addFriend(user1.getId(), user2.getId(), FriendshipStatus.UNCONFIRMED);

        Map<Integer, FriendshipStatus> friends = friendshipStorage.getFriendsWithStatus(user1.getId());
        assertThat(friends).containsEntry(user2.getId(), FriendshipStatus.UNCONFIRMED);

        friendshipStorage.removeFriend(user1.getId(), user2.getId());

        Map<Integer, FriendshipStatus> updatedFriends = friendshipStorage.getFriendsWithStatus(user1.getId());
        assertThat(updatedFriends).doesNotContainKey(user2.getId());
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        Optional<User> foundUser = userStorage.getUserById(999);
        assertThat(foundUser).isEmpty();
    }

    @Test
    void shouldConfirmFriendship() {
        User user1 = userStorage.createUser(testUser);

        User user2 = new User();
        user2.setEmail("friend@example.com");
        user2.setLogin("friendLogin");
        user2.setBirthday(LocalDate.of(1992, 3, 10));
        user2 = userStorage.createUser(user2);

        friendshipStorage.addFriend(user1.getId(), user2.getId(), FriendshipStatus.UNCONFIRMED);

        friendshipStorage.confirmFriendship(user2.getId(), user1.getId());

        Map<Integer, FriendshipStatus> friendships = friendshipStorage.getFriendsWithStatus(user1.getId());
        assertThat(friendships.get(user2.getId())).isEqualTo(FriendshipStatus.CONFIRMED);
    }
}
