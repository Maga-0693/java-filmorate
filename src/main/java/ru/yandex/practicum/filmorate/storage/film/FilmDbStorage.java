package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final LikeDbStorage likeDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         FilmGenreDbStorage filmGenreDbStorage,
                         LikeDbStorage likeDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreDbStorage = filmGenreDbStorage;
        this.likeDbStorage = likeDbStorage;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre ->
                    filmGenreDbStorage.addGenreToFilm(film.getId(), genre.getId()));
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        filmGenreDbStorage.removeGenresFromFilm(film.getId());
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre ->
                    filmGenreDbStorage.addGenreToFilm(film.getId(), genre.getId()));
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.*, m.mpa_name, m.description AS mpa_description " +
                "FROM films f JOIN mpa_ratings m ON f.mpa_id = m.mpa_id"; // Исправлено mpa на mpa_ratings

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));

            Mpa mpa = new Mpa(
                    rs.getInt("mpa_id"),
                    rs.getString("mpa_name"),
                    rs.getString("mpa_description")
            );
            film.setMpa(mpa);

            return film;
        });
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT f.*, m.mpa_name, m.description AS mpa_description " +
                "FROM films f JOIN mpa_ratings m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Film film = new Film();
                film.setId(rs.getInt("film_id"));  // Используем film_id вместо id
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));

                Mpa mpa = new Mpa(
                        rs.getInt("mpa_id"),
                        rs.getString("mpa_name"),
                        rs.getString("mpa_description")
                );
                film.setMpa(mpa);

                return film;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
