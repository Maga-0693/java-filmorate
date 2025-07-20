package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY mpa_id";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> mapRowToMpa(resultSet));
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> mapRowToMpa(resultSet), id);
            return Optional.ofNullable(mpa);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM mpa_ratings WHERE mpa_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private Mpa mapRowToMpa(ResultSet resultSet) throws SQLException {
        return new Mpa(
                resultSet.getInt("mpa_id"),
                resultSet.getString("mpa_name"),
                resultSet.getString("description")
        );
    }
}