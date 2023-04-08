package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Long id);

    List<Film> getAllFilms();

    boolean isContains(Long id);

    Film deleteFilm(long id);

    List<Film> getTopFilms(int count);
}
