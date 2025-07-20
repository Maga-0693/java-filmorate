package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, (resultSet, rowNum) ->
                new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name")));
    }

    @Override
    public Genre getGenreById(int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) ->
                new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name")), id);
    }

    @Override
    public List<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT g.genre_id, g.genre_name FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? ORDER BY g.genre_id";
        return jdbcTemplate.query(sql, (resultSet, rowNum) ->
                new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name")), filmId);
    }

    @Override
    public void addFilmGenre(int filmId, int genreId) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public void removeFilmGenres(int filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}