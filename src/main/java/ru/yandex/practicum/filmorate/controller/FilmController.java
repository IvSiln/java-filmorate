package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        List<Film> films = filmService.getAllFilms();
        return ResponseEntity.ok(films);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable long id) {
        Optional<Film> film = filmService.getFilmById(id);
        log.trace("Requested film: {}", film);
        return film.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film newFilm) {
        Film createdFilm = filmService.createFilm(newFilm);
        log.trace("Добавлен фильм: " + createdFilm);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film updateFilm) {
        Film updatedFilm = filmService.updateFilm(updateFilm);
        log.trace("Обновлен фильм: " + updatedFilm);
        return ResponseEntity.ok(updatedFilm);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Film> deleteFilm(@PathVariable long id) {
        Optional<Film> deletedFilm = filmService.deleteFilm(id);
        log.trace("Deleted film: {}", deletedFilm);
        return deletedFilm.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        log.trace(String.format("Пользователь с ID: %d отметил лайком фильм с ID: %d", userId, id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
        log.trace(String.format("Пользователь с ID: %d удалил лайк фильма с ID: %d ", userId, id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getTopFilms(@RequestParam(value = "count", defaultValue = "10", required = false) @Positive(message = "count должен быть больше 0") Integer count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count должен быть целым числом больше 0, получено " + count);
        }
        List<Film> topFilms = filmService.getTopFilms(count);
        return ResponseEntity.ok(topFilms);
    }
}