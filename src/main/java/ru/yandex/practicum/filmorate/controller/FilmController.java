package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    static final LocalDate START_DATE = LocalDate.of(1985, 12, 28);
    Map<Integer, Film> filmMap;
    private int filmId;

    public FilmController() {
        filmId = 0;
        filmMap = new HashMap<>();
    }

    public Integer generateId() {
        return filmId++;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        isValidDate(film);
        film.setId(generateId());
        filmMap.put(film.getId(), film);
        log.trace("Добавален новый фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (filmMap.containsKey(film.getId())) {
            filmMap.put(film.getId(), film);
            log.trace("Обновлен фильм: {}",film);
            return film;
        } else throw new ValidationException("Фильма с таким id нет в базе");
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    private void isValidDate(Film film) {
        if (film.getReleaseDate().isBefore(START_DATE)) {
            log.warn(film.getReleaseDate().toString());
            throw new ValidationException("Дата выхода фильма не может быть раньше " + START_DATE);
        }
        for (Film valueComparison : filmMap.values()) {
            if (valueComparison.getName().equals(film.getName())) {
                throw new ValidationException("Фильм уже есть в нашей базе");
            }
        }
    }

}
