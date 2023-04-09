package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (userStorage.getUserById(userId) != null) {
                film.getLikes().add(userId);
            } else {
                throw new NotFoundException("Пользователь c ID=" + userId + " не найден!");
            }
        } else {
            throw new NotFoundException("Фильм c ID=\" + filmId + \" не найден!");
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (film.getLikes().contains(userId)) {
                film.getLikes().remove(userId);
            } else {
                throw new NotFoundException("Лайк от пользователя c ID=" + userId + " не найден!");
            }
        } else {
            throw new NotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
    }

    public List<Film> getTopFilms(Integer count) {
        if (count <= 0) {
            throw new ValidationException("Количество фильмов для вывода не должно быть отрицательным");
        }
        return filmStorage.getFilms().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
