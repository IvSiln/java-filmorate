package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.sql.SQLException;
import java.util.List;

@Service
public class FilmDbService {
    private final FilmDbStorage filmStorage;

    private final UserDbService userService;

    @Autowired
    public FilmDbService(FilmDbStorage filmStorage, UserDbService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }


    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(long id) {
        if (filmStorage.getMpaById(id) != null) {
            return filmStorage.getMpaById(id);
        } else {
            throw new NotFoundException("Не найден MPA с ID: " + id);
        }
    }

    public Genre getGenreById(long id) {
        if (filmStorage.getGenreById(id) != null) {
            return filmStorage.getGenreById(id);
        } else {
            throw new NotFoundException("Не найден MPA с ID: " + id);
        }
    }

    public Film getFilmById(long id) {
        if (filmStorage.getFilmById(id) == null) {
            throw new NotFoundException("Фильм с ID: " + id + "не найден");
        }
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film newFilm) throws SQLException {
        return filmStorage.createFilm(newFilm);
    }

    public Film updateFilm(Film updateFilm) throws SQLException {
        Film film = filmStorage.updateFilm(updateFilm);
        if (film == null) {
            throw new NotFoundException("Произошла ошибка при обновлении фильма с ID:" + updateFilm.getId());
        } else {
            return film;
        }
    }

    public Film deleteFilm(long deleteId) {
        if (filmStorage.deleteFilm(deleteId).isPresent()) {
            return filmStorage.deleteFilm(deleteId).get();
        } else {
            throw new NotFoundException("Произошла ошибка при удалении фильма с ID:" + deleteId);
        }
    }

    public void addLike(Long filmId, Long userId) {
        if (!userService.isUserExist(userId)) {
            throw new NotFoundException("Нет пользователя c ID:" + userId);
        }

        if (!filmStorage.isFilmExist(filmId)) {
            throw new NotFoundException("Нет фильма c ID:" + filmId);
        }

        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (!userService.isUserExist(userId)) {
            throw new NotFoundException("Нет пользователя c ID:" + userId);
        }

        if (!filmStorage.isFilmExist(filmId)) {
            throw new NotFoundException("Нет фильма c ID:" + filmId);
        }

        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }
}
