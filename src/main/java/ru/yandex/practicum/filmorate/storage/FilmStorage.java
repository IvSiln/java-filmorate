package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> deleteFilm(long id);

    List<Film> getAllFilms();

    List<Film> getTopFilms(Integer count);

    Optional<Film> getFilmById(long filmId);

    List<Genre> getAllGenres();

    List<Mpa> getAllMpa();

    Mpa getMpaById(long id);

    Genre getGenreById(long id);

    boolean isFilmExist(Long filmId);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}
