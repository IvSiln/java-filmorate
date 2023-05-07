package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film createFilm(Film film) throws SQLException;

    Film updateFilm(Film film) throws SQLException;

    Optional<Film> deleteFilm(long id);

    List<Film> getAllFilms();
}
