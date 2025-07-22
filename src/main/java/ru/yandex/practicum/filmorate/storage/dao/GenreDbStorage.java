package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Genre> genreRowMapper() {
        return new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("genre_name")
                );
            }
        };
    }

    @Override
    public Optional<Genre> getById(Integer id) {
        String query = "SELECT genre_id, genre_name FROM Genre WHERE genre_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, genreRowMapper(), id));
    }

    @Override
    public List<Genre> getAll() {
        String query = "SELECT genre_id, genre_name FROM Genre";
        return jdbcTemplate.query(query, genreRowMapper());
    }

    @Override
    public List<Genre> getGenresByFilmId(int filmId) {
        String query = "SELECT g.genre_id, g.genre_name " +
                "FROM Genre g " +
                "JOIN Genre_Film gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(query, genreRowMapper(), filmId);

        Set<Genre> uniqueGenres = new LinkedHashSet<>(genres);
        return new ArrayList<>(uniqueGenres);
    }
}