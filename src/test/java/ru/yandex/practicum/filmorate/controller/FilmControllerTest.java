package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exception.CustomValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    private Film validFilm;
    private Film invalidFilm;

    @BeforeEach
    void setUp() {
        validFilm = new Film(1, "Форсаж", "Боевик с элементами фантастики",
                LocalDate.of(2000, 10, 8), 135, null,
                Collections.emptyList(), new Mpa(1, "G"));

        invalidFilm = new Film(22, "", "", LocalDate.of(1777, 9, 2),
                -1, null, null, null);
    }

    @Test
    void givenRightFilm_whenAddFilm_thenSuccess() {
        when(filmService.addFilm(any(Film.class))).thenReturn(validFilm);

        ResponseEntity<Film> response = filmController.addFilm(validFilm);
        Film result = response.getBody();

        assertEquals(validFilm, result);
        assertEquals(201, response.getStatusCodeValue());
        verify(filmService, times(1)).addFilm(validFilm);
    }

    @Test
    void givenRightFilm_whenGetFilms_thenGetListOfFilms() {
        when(filmService.getFilms()).thenReturn(List.of(validFilm));

        List<Film> films = filmController.getFilms();

        assertEquals(1, films.size());
        assertEquals(validFilm, films.get(0));
    }

    @Test
    void givenWrongFilm_whenPostRequest_thenThrowException() {
        doThrow(new CustomValidationException("Invalid film data"))
                .when(filmService).addFilm(invalidFilm);

        assertThrows(CustomValidationException.class,
                () -> filmController.addFilm(invalidFilm));

        verify(filmService, times(1)).addFilm(invalidFilm);
    }

    @Test
    void givenRightFilm_whenUpdateFilm_thenSuccess() {
        Film updatedFilm = new Film(1, "Updated", "Desc",
                LocalDate.now(), 100, null, null, null);

        when(filmService.updateFilm(any(Film.class))).thenReturn(updatedFilm);

        Film result = filmController.updateFilm(updatedFilm);

        assertEquals(updatedFilm, result);
        verify(filmService, times(1)).updateFilm(updatedFilm);
    }

    @Test
    void givenFilmWithNonExistentGenre_whenAddFilm_thenThrowNotFoundException() {
        Film filmWithBadGenre = new Film(1, "Film", "Desc",
                LocalDate.now(), 120, null,
                List.of(new Genre(999, "Bad")), new Mpa(1, "G"));

        when(filmService.addFilm(filmWithBadGenre))
                .thenThrow(new NotFoundException("Genre not found"));

        assertThrows(NotFoundException.class,
                () -> filmController.addFilm(filmWithBadGenre));
    }

    @Test
    void givenNonExistentFilmId_whenGetFriends_thenThrowNotFoundException() {
        when(filmService.getFriendsByFilmId(999))
                .thenThrow(new NotFoundException("Film not found"));

        assertThrows(NotFoundException.class,
                () -> filmController.getFriendsByFilmId(999));
    }
}