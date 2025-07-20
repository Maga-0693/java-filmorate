package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FilmGenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public void addGenreToFilm(int filmId, int genreId) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    public void removeGenresFromFilm(int filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    public List<Integer> getFilmGenreIds(int filmId) {
        String sql = "SELECT genre_id FROM film_genres WHERE film_id = ? ORDER BY genre_id";
        return jdbcTemplate.queryForList(sql, Integer.class, filmId);
    }

    public void updateFilmGenres(int filmId, List<Integer> genreIds) {
        removeGenresFromFilm(filmId);

        if (genreIds != null && !genreIds.isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

            jdbcTemplate.batchUpdate(sql, genreIds, genreIds.size(),
                    (ps, genreId) -> {
                        ps.setInt(1, filmId);
                        ps.setInt(2, genreId);
                    });
        }
    }
}