package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT g.genre_id, g.name FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? ORDER BY g.genre_id";

        return jdbcTemplate.query(sql, (genreResultSet, rowNum) ->
                        new Genre(
                                genreResultSet.getInt("genre_id"),
                                genreResultSet.getString("name")
                        ),
                filmId);
    }

    public void updateFilmGenres(int filmId, List<Integer> genreIds) {
        removeGenresFromFilm(filmId);

        if (genreIds != null && !genreIds.isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

            jdbcTemplate.batchUpdate(sql, genreIds, genreIds.size(),
                    (preparedStatement, genreId) -> {
                        preparedStatement.setInt(1, filmId);
                        preparedStatement.setInt(2, genreId);
                    });
        }
    }

    public Map<Integer, List<Genre>> getGenresForFilms(List<Integer> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inClause = filmIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = String.format(
                "SELECT fg.film_id, g.genre_id, g.name FROM film_genres fg " +
                        "JOIN genres g ON fg.genre_id = g.genre_id " +
                        "WHERE fg.film_id IN (%s) ORDER BY fg.film_id, g.genre_id",
                inClause
        );

        return jdbcTemplate.query(sql, (resultSet) -> {
            Map<Integer, List<Genre>> filmsGenresMap = new HashMap<>();

            while (resultSet.next()) {
                int currentFilmId = resultSet.getInt("film_id");
                Genre genre = new Genre(
                        resultSet.getInt("genre_id"),
                        resultSet.getString("name")
                );

                filmsGenresMap
                        .computeIfAbsent(currentFilmId, k -> new ArrayList<>())
                        .add(genre);
            }

            return filmsGenresMap;
        });
    }
}
