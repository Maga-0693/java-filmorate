package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.api.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Mpa> mpaRowMapper() {
        return new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
            }
        };
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        String query = "SELECT mpa_id, mpa_name FROM Mpa WHERE mpa_id=?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, mpaRowMapper(), id));
    }

    @Override
    public List<Mpa> getAllMpa() {
        String query = "SELECT mpa_id, mpa_name FROM Mpa";
        return jdbcTemplate.query(query, mpaRowMapper());
    }
}