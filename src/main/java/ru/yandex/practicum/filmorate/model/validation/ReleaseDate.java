package ru.yandex.practicum.filmorate.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDate {
    String message() default "Invalid release date. The date should not be before 28 December 1895.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}