package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDbService;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmDbService filmService;

    @Autowired
    public FilmController(FilmDbService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        Film film = filmService.getFilmById(id);
        log.trace("Запрошен фильм: " + film);
        return film;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film newFilm) throws SQLException {
        Film createdFilm = filmService.createFilm(newFilm);
        log.trace("Добавлен фильм: " + createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updateFilm) throws SQLException {
        Film updatedFilm = filmService.updateFilm(updateFilm);
        log.trace("Обновлен фильм: " + updatedFilm);
        return updatedFilm;
    }

    @DeleteMapping("/{id}")
    public Film deleteFilm(@PathVariable long id) {
        Film deletedFilm = filmService.deleteFilm(id);
        log.trace("Удален фильм: {}", deletedFilm);
        return deletedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity addLike(@PathVariable Long id,
                                  @PathVariable Long userId) {
        filmService.addLike(id, userId);
        log.trace(String.format("Пользователь с ID: %d отметил лайком фильм с ID: %d", userId, id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity deleteLike(@PathVariable Long id,
                                     @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
        log.trace(String.format("Пользователь с ID: %d удалил лайк фильма с ID: %d ", userId, id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count должен быть целым числом больше 0, получено " + count);
        }
        List<Film> topFilms = filmService.getTopFilms(count);
        return topFilms;
    }
}
