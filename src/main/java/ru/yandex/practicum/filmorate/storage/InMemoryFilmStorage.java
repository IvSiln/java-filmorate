package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;
    private final LocalDate START_DATE = LocalDate.of(1895, 12, 28);
    private Long currentId;

    @Autowired
    public InMemoryFilmStorage() {
        films = new HashMap<>();
        currentId = 0L;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        isValid(film);
        film.setId(++currentId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        check(film.getId());
        isValid(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(Long filmId) {
        check(filmId);
        return films.get(filmId);
    }

    @Override
    public Film delete(Long filmId) {
        check(filmId);
        return films.remove(filmId);
    }

    private void check(Long id) {
        if (id == null) {
            throw new ValidationException("Фильму не присвоен id");
        }
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с ID=" + id + " не найден!");
        }
    }

    private void isValid(Film film) {
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не должно быть пустым!");
        }
        if ((film.getDescription().length()) > 200 || (film.getDescription().isEmpty())) {
            throw new ValidationException("Максимальная длинна описания 200 знаков или пустое: " + film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(START_DATE)) {
            throw new ValidationException("Не возможно добавить фильмы с датой релиза раньше: " + film.getReleaseDate());
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Задана не верная длительность: " + film.getDuration());
        }
    }
}
