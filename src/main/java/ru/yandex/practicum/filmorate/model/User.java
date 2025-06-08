package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

@Data
public class User {
    private int id;

    @NotBlank(message = "Адрес электронной почты должен быть действующим")
    @Email(message = "Электронная почта должна содержать символ @")
    private String email;

    @NotBlank(message = "Логин не может содержать пробелы")
    @Pattern(regexp = "\\S+", message = "Логин не может быть пустым")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
