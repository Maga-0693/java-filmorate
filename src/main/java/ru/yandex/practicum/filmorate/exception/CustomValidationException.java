package ru.yandex.practicum.filmorate.exception;

public class CustomValidationException extends RuntimeException {
    public CustomValidationException(String s) {

        super(s);
    }
}