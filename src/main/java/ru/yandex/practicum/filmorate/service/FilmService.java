package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final UserService userService;
    private long counterId;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, UserService userService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.userService = userService;
        counterId = 0;
    }

    public long getCounterId() {
        return counterId;
    }

    private long setCounterId() {
        ++counterId;
        return counterId;
    }

    public Film createFilm(Film film) {
        if (inMemoryFilmStorage.getFilmByName(film.getName()).isPresent()) {
            throw new ValidationException("Уже есть фильм с названием: " + film.getName());
        } else {
            film.setId(setCounterId());
            inMemoryFilmStorage.createFilm(film);
            return film;
        }
    }

    public Film updateFilm(Film film) {
        Film currentFilm = getFilmById(film.getId());
        Film updatedFilm = inMemoryFilmStorage.updateFilm(film);
        return updatedFilm;
    }

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film getFilmById(long filmId) {
        return inMemoryFilmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Нет фильма с ID:" + filmId));
    }

    public Film getFilmByName(String filmName) {
        return inMemoryFilmStorage.getFilmByName(filmName)
                .orElseThrow(() -> new NotFoundException("Нет фильма c названием:" + filmName));
    }

    public Film deleteFilm(long id) {
        return inMemoryFilmStorage.deleteFilm(id)
                .orElseThrow(() -> new NotFoundException("Произошла ошибка при удалении фильма с ID:" + id));
    }

    public void addLike(long filmId, long userId) {
        Film currentFilm = getFilmById(filmId);
        User currentUser = userService.getUserById(userId);

        if (!currentFilm.addLike(userId)) {
            throw new ValidationException(String.format("Пользователь с ID: %d  уже уже поставил лайк фильму с ID: %d " + userId, filmId));
        }
        inMemoryFilmStorage.updateFilm(currentFilm);
    }

    public void deleteLike(long filmId, long userId) {
        Film currentFilm = getFilmById(filmId);
        User currentUser = userService.getUserById(userId);

        if (!currentFilm.deleteLike(userId)) {
            throw new ValidationException(String.format("Пользователь с ID: %d не ставил лайк фильму с ID: %d", userId, filmId));
        }
        inMemoryFilmStorage.updateFilm(currentFilm);
    }

    public List<Film> getTopFilms(int count) {
        return inMemoryFilmStorage.getTopFilms(count);
    }
}
