package ru.yandex.practicum.filmorate.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDate {
    String message() default "Неверная дата выпуска. Дата не должна быть раньше 28 декабря 1895.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}