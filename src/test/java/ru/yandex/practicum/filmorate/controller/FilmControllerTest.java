package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FilmService filmService;

    @Mock
    private MpaService mpaService;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private FilmController filmController;

    private Film film;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();

        film = new Film(1, "Film", "Description",
                LocalDate.now(), 120, 0,
                Collections.emptyList(), new Mpa(1, "G"));
    }

    @Test
    void givenRightFilm_whenAddFilm_thenMapFilled() throws Exception {
        when(filmService.addFilm(any(Film.class))).thenReturn(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Film"));
    }

    @Test
    void givenRightFilm_whenGetFilms_thenGetListOfFilms() throws Exception {
        when(filmService.getFilms()).thenReturn(Collections.singletonList(film));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Film"));
    }

    @Test
    void givenRightFilm_whenUpdateFilm_thenMapValueUpdated() throws Exception {
        Film updatedFilm = new Film(1, "Updated Film", "Updated Description",
                LocalDate.now(), 150, 0,
                Collections.emptyList(), new Mpa(1, "G"));

        when(filmService.updateFilm(any(Film.class))).thenReturn(updatedFilm);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Film"));
    }

    @Test
    void givenFilmWithNonExistentGenre_whenAddFilm_thenThrowNotFoundException() throws Exception {
        Film filmWithBadGenre = new Film(1, "Film", "Description",
                LocalDate.now(), 120, 0,
                Collections.singletonList(new Genre(999, "Bad Genre")),
                new Mpa(1, "G"));

        when(genreService.getGenreById(999)).thenThrow(new NotFoundException("Genre not found"));
        when(mpaService.getMpaById(1)).thenReturn(new Mpa(1, "G"));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmWithBadGenre)))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenFilmWithNonExistentMpa_whenAddFilm_thenThrowNotFoundException() throws Exception {
        Film filmWithBadMpa = new Film(1, "Film", "Description",
                LocalDate.now(), 120, 0,
                Collections.emptyList(),
                new Mpa(999, "Bad MPA"));

        when(mpaService.getMpaById(999)).thenThrow(new NotFoundException("MPA not found"));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmWithBadMpa)))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNonExistentFilmId_whenGetFriends_thenThrowNotFoundException() throws Exception {
        when(filmService.getFriendsByFilmId(999)).thenThrow(new NotFoundException("Film not found"));

        mockMvc.perform(get("/films/999/friends"))
                .andExpect(status().isNotFound());
    }
}