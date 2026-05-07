package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Genre> mapper = (rs, rowNum) -> {
        Genre genre = new Genre();
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("name"));
        return genre;
    };

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres ORDER BY id";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Genre getGenreById(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";

        List<Genre> result = jdbcTemplate.query(sql, mapper, id);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Genre> getGenresByIds(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        String inSql = String.join(",", ids.stream()
                .map(id -> "?")
                .toList());

        String sql = "SELECT * FROM genres WHERE id IN (" + inSql + ")";

        return jdbcTemplate.query(sql, mapper, ids.toArray());
    }
}