package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;
    private Long currentId;

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
        film.setId(++currentId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        check(film.getId());
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
}
