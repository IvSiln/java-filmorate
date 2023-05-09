package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService implements FilmStorage {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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

    @Override
    public boolean isFilmExist(Long filmId) {
        return false;
    }

    public Optional<Film> getFilmById(long id) {
        if (filmStorage.getFilmById(id).isEmpty()) {
            throw new NotFoundException("Фильм с ID: " + id + "не найден");
        }
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film newFilm) {
        return filmStorage.createFilm(newFilm);
    }

    public Film updateFilm(Film updateFilm) {
        Film film = filmStorage.updateFilm(updateFilm);
        if (film == null) {
            throw new NotFoundException("Произошла ошибка при обновлении фильма с ID:" + updateFilm.getId());
        } else {
            return film;
        }
    }

    @Override
    public Optional<Film> deleteFilm(long deleteId) {
        if (filmStorage.deleteFilm(deleteId).isPresent()) {
            return filmStorage.deleteFilm(deleteId);
        } else {
            throw new NotFoundException("Произошла ошибка при удалении фильма с ID:" + deleteId);
        }
    }

    public void addLike(Long filmId, Long userId) {
        if (!userStorage.isUserExist(userId)) {
            throw new NotFoundException("Нет пользователя c ID:" + userId);
        }

        if (!filmStorage.isFilmExist(filmId)) {
            throw new NotFoundException("Нет фильма c ID:" + filmId);
        }

        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (!userStorage.isUserExist(userId)) {
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
