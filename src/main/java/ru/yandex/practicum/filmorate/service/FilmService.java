package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {
    static final LocalDate START_DATE = LocalDate.of(1985, 12, 28);
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public boolean addLike(Long filmId, Long userId) {
        return filmStorage.getFilm(filmId).getLikes().add(userId);
    }

    public boolean deleteLike(Long filmId, Long userId) {
        return filmStorage.getFilm(filmId).getLikes().remove(userId);
    }

    public Film createFilm(Film film) {
        isValidFilm(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        isValidFilm(film);
        if (filmStorage.isContains(film.getId())) {
            return filmStorage.updateFilm(film);
        }
        throw new NotFoundException("Фильм не найден");
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long filmId) {
        if (filmStorage.isContains(filmId)) {
            return filmStorage.getFilm(filmId);
        }
        throw new NotFoundException("Фильм не найден");
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    public void isValidFilm(Film film) {
        if (film.getReleaseDate().isBefore(START_DATE)) {
            throw new ValidationException("Дата выхода фильма не может быть раньше " + START_DATE);
        }
    }
}
