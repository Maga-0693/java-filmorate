package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
public class FilmorateApplicationTests {
	@Autowired
	private UserDbStorage userStorage;

	@Test
	public void testFindUserById() {
		User testUser = new User();
		testUser.setEmail("test@example.com");
		testUser.setLogin("testLogin");
		testUser.setBirthday(LocalDate.now());

		User createdUser = userStorage.createUser(testUser);

		Optional<User> foundUser = userStorage.getUserById(createdUser.getId());

		assertThat(foundUser)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", createdUser.getId())
				);
	}
}