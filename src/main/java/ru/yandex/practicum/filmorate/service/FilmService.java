package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {
    static final LocalDate START_DATE = LocalDate.of(1985, 12, 28);
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public boolean addLike(Long filmId, Long userId) {
        return inMemoryFilmStorage.getFilm(filmId).getLikes().add(userId);
    }

    public boolean deleteLike(Long filmId, Long userId) {
        return inMemoryFilmStorage.getFilm(filmId).getLikes().remove(userId);
    }

    public Film createFilm(Film film) {
        isValidFilm(film);
        return inMemoryFilmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        isValidFilm(film);
        if (inMemoryFilmStorage.isContains(film.getId())) {
            return inMemoryFilmStorage.updateFilm(film);
        }
        throw new NotFoundException("Фильм не найден");
    }

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film getFilmById(long filmId) {
        if (inMemoryFilmStorage.isContains(filmId)) {
            return inMemoryFilmStorage.getFilm(filmId);
        }
        throw new NotFoundException("Фильм не найден");
    }

    public List<Film> getTopFilms(int count) {
        return inMemoryFilmStorage.getTopFilms(count);
    }

    public void isValidFilm(Film film) {
        if (film.getReleaseDate().isBefore(START_DATE)) {
            throw new ValidationException("Дата выхода фильма не может быть раньше " + START_DATE);
        }
    }
}
