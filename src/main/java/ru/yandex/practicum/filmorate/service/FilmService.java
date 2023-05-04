package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
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
        Optional<Film> optionalFilm = Optional.ofNullable(filmStorage.getFilmById(filmId));
        if (optionalFilm.isEmpty()) throw new NotFoundException("Фильм c ID=\" + filmId + \" не найден!");

        Optional<User> optionalUser = Optional.ofNullable(userStorage.getUserById(userId));
        if (optionalUser.isEmpty()) throw new NotFoundException("Пользователь c ID=" + userId + " не найден!");

        optionalFilm.get().getLikes().add(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Optional<Film> optionalFilm = Optional.ofNullable(filmStorage.getFilmById(filmId));
        if (optionalFilm.isEmpty()) throw new NotFoundException("Фильм c ID=\" + filmId + \" не найден!");

        Optional<User> optionalUser = Optional.ofNullable(userStorage.getUserById(userId));
        if (optionalUser.isEmpty()) throw new NotFoundException("Пользователь c ID=" + userId + " не найден!");

        optionalFilm.get().getLikes().remove(userId);

    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
