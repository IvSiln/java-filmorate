package ru.yandex.practicum.filmorate.storage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
@Slf4j
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private static final int INSERTBATCHSIZE = 100;// размер пакета для операций вставки (batch insert).
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Film makeFilm(ResultSet rs) {
        try {
            Long filmId = rs.getLong("film_id");
            String name = rs.getString("name");
            String description = rs.getString("description");
            LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
            int duration = rs.getInt("duration");
            long rate = rs.getLong("rate");
            Mpa mpa = getMpaById(rs.getLong("mpa_id"));
            List<Genre> genres = getGenresByFilmId(filmId);
            Set<Long> likes = getLikes(filmId);
            return new Film(filmId, name, description, releaseDate, duration, rate, mpa, genres, likes);
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Film createFilm(Film film) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("rate", film.getRate());
        parameters.put("mpa_id", film.getMpa().getId());
        SimpleJdbcInsert insertIntoFilm = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Long filmId = (Long) insertIntoFilm.executeAndReturnKey(parameters);

        addGenresByFilmId(filmId, film.getGenres());

        return getFilmById(filmId).orElse(null);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE FILMS SET name=?, description=?, release_date=?, duration=?, rate=?, mpa_id=? WHERE film_id=?";
        if (jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId()) > 0) {
            deleteGenresByFilmId(film.getId());
            addGenresByFilmId(film.getId(), film.getGenres());
            return getFilmById(film.getId()).orElse(null);
        }
        return null;
    }

    public void deleteGenresByFilmId(long filmId) {
        String sqlQuery = "DELETE FROM FILMGENRES WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @SneakyThrows
    public void addGenresByFilmId(long filmId, List<Genre> genres) {
        DataSource ds = jdbcTemplate.getDataSource();
        assert ds != null;
        Connection connection = ds.getConnection();
        connection.setAutoCommit(false);
        String sqlQuery = "INSERT INTO FILMGENRES (film_id, genre_id) values (?,?)";
        jdbcTemplate.batchUpdate(sqlQuery,
                genres,
                INSERTBATCHSIZE,
                (PreparedStatement ps, Genre genre) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genre.getId());
                });
    }

    @Override
    public Optional<Film> deleteFilm(long id) {
        Optional<Film> filmOptional = getFilmById(id);
        if (filmOptional.isPresent()) {
            String sqlQuery = "DELETE FROM FILMS WHERE film_id = ?";
            if (jdbcTemplate.update(sqlQuery, id) > 0) {
                return filmOptional;
            }
        }
        return Optional.empty();
    }

    public Optional<Film> getFilmById(long filmId) {
        String sqlQuery = "SELECT * FROM FILMS WHERE film_id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), filmId);

        if (!films.isEmpty()) {
            return Optional.of(films.get(0));
        } else {
            log.info("Фильм с идентификатором {} не найден.", filmId);
            return Optional.empty();
        }
    }


    public Set<Long> getDistinctGenreIdsByFilmId(long filmId) {
        String sqlQuery = "SELECT DISTINCT genre_id FROM GENRES WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Long.class, filmId));
    }

    public List getGenresByFilmId(long filmId) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id IN ( SELECT DISTINCT genre_id FROM FILMGENRES WHERE film_id = ?)";
        List genres;
        genres = jdbcTemplate.query(sqlQuery,
                (rs, rowNum) ->
                        new Genre(
                                rs.getLong("genre_id"),
                                rs.getString("name")),
                filmId);
        log.info("У фильм с ID: {} жанров: {}", filmId, genres.size());
        return genres;
    }

    public Set<Long> getLikes(long filmId) {
        String sqlQuery = "SELECT DISTINCT user_id FROM LIKES WHERE film_id = ?";
        return new HashSet<Long>(jdbcTemplate.queryForList(sqlQuery, Long.class, filmId));
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) ->
                        new Genre(
                                rs.getLong("genre_id"),
                                rs.getString("name")));
    }

    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) ->
                        new Mpa(
                                rs.getLong("mpa_id"),
                                rs.getString("name")));
    }

    public Mpa getMpaById(long id) {
        String sqlQuery = "SELECT * FROM MPA WHERE mpa_id=?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            log.info("Найден MPA: {} {}", userRows.getString("mpa_id"), userRows.getString("name"));
            Mpa mpa = new Mpa(
                    userRows.getLong("mpa_id"),
                    userRows.getString("name"));
            return mpa;
        } else {
            log.info("MPA с идентификатором {} не найден.", id);
            return null;
        }
    }

    public Genre getGenreById(long id) {
        String sqlQuery = "SELECT * FROM GENRES WHERE genre_id=?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            log.info("Найден жанр: {} {}", userRows.getString("genre_id"), userRows.getString("name"));
            Genre genre = new Genre(
                    userRows.getLong("genre_id"),
                    userRows.getString("name"));
            return genre;
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            return null;
        }
    }

    public boolean isFilmExist(Long filmId) {
        String sqlQuery = "SELECT 1 FROM FILMS WHERE film_id=?";
        return Boolean.TRUE.equals(jdbcTemplate.query(sqlQuery,
                (ResultSet rs) -> {
                    if (rs.next()) {
                        return true;
                    }
                    return false;
                }, filmId
        ));
    }

    public void addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO LIKES (film_id, user_id) values (?,?)";
        increaseFilmRate(filmId);
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";
        decreaseFilmRate(filmId);
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        String sqlQuery = "SELECT * FROM FILMS  ORDER BY rate DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> makeFilm(rs),
                count);
    }

    public Long getFilmRating(long filmId) {
        String sqlQuery = "SELECT COUNT (DISTINCT user_id )  FROM LIKES WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, Long.class, filmId);
    }

    public boolean increaseFilmRate(long filmId) {
        String sqlQuery = "UPDATE FILMS SET rate = rate + 1 WHERE film_id=?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }

    public boolean decreaseFilmRate(long filmId) {
        String sqlQuery = "UPDATE FILMS SET rate = rate - 1 WHERE film_id=?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }

    public String getMpaByFilmId(Long filmId) {
        String sqlQuery = "SELECT name FROM mpa WHERE mpa_id = (SELECT mpa_id FROM FILMMPA WHERE film_id=?)";
        return jdbcTemplate.queryForObject(sqlQuery, String.class, filmId);
    }
}