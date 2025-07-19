package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
class FilmoRateApplicationTests {
	private final UserDbStorage userStorage;

	@Autowired
	public FilmoRateApplicationTests(UserDbStorage userStorage) {
		this.userStorage = userStorage;
	}

	@Test
	public void testFindUserById() {
		User testUser = User.builder()
				.email("test@example.com")
				.login("testLogin")
				.birthday(java.time.LocalDate.now())
				.build();

		User createdUser = userStorage.createUser(testUser);

		Optional<User> foundUser = userStorage.getUserById(createdUser.getId());
		
		assertThat(foundUser)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", createdUser.getId())
				);
	}
}
