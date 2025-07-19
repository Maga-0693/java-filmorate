package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.ArrayList;
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
        List<Mpa> mpaList = new ArrayList<>();

        jdbcTemplate.query(sql, (rs, rowNum) ->
                mpaList.add(Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .description(rs.getString("description"))
                        .build())
        );

        return mpaList;
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                            Mpa.builder()
                                    .id(rs.getInt("mpa_id"))
                                    .name(rs.getString("mpa_name"))
                                    .description(rs.getString("description"))
                                    .build(),
                    id);
            return Optional.ofNullable(mpa);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
