package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.CustomValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    private Film film;
    private Film film2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();

        film = new Film(22, "", "", LocalDate.of(1777, 9, 2),
                -1, null, null, null);
        film2 = new Film(1, "Марко Поло", "Комедия про похождения друзей",
                LocalDate.of(2000, 10, 8), 135, null, Collections.emptyList(), new Mpa(1, "G"));
    }

    @Test
    void givenRightFilm_whenAddFilm_thenMapFilled() throws Exception {
        when(filmService.addFilm(any(Film.class))).thenReturn(film2);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().isOk());
    }

    @Test
    void givenRightFilm_whenGetFilms_thenGetListOfFilms() throws Exception {
        when(filmService.getFilms()).thenReturn(List.of(film2));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Марко Поло"));
    }

    @Test
    void givenWrongFilm_whenPostRequest_thenThrowException() throws Exception {
        when(filmService.addFilm(any(Film.class))).thenThrow(new CustomValidationException("Validation error"));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenRightFilm_whenUpdateFilm_thenMapValueUpdated() throws Exception {
        Film updatedFilm = new Film(1, "Updated Film", "Updated desc",
                LocalDate.of(2000, 10, 8), 135, null, Collections.emptyList(), new Mpa(1, "G"));

        when(filmService.updateFilm(any(Film.class))).thenReturn(updatedFilm);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Film"));
    }

    @Test
    void givenFilmWithNonExistentGenre_whenAddFilm_thenThrowNotFoundException() throws Exception {
        Film filmWithBadGenre = new Film(1, "Film", "Desc",
                LocalDate.now(), 120, 0,
                Collections.singletonList(new Genre(999, "Bad")),
                new Mpa(1, "G"));

        when(filmService.addFilm(any(Film.class))).thenThrow(new NotFoundException("Genre not found"));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmWithBadGenre)))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenFilmWithNonExistentMpa_whenAddFilm_thenThrowNotFoundException() throws Exception {
        Film filmWithBadMpa = new Film(1, "Film", "Desc",
                LocalDate.now(), 120, 0,
                Collections.emptyList(),
                new Mpa(999, "Bad"));

        when(filmService.addFilm(any(Film.class))).thenThrow(new NotFoundException("MPA not found"));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmWithBadMpa)))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNonExistentFilmId_whenGetFriends_thenThrowNotFoundException() throws Exception {
        when(filmService.getFriendsByFilmId(anyInt())).thenThrow(new NotFoundException("Film not found"));

        mockMvc.perform(get("/films/999/friends"))
                .andExpect(status().isNotFound());
    }
}