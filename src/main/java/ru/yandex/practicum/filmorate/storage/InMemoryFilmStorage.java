package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Long counterId;
    private List<Film> films = new ArrayList<>();


    @Override
    public Film createFilm(Film film) {
        films.add(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        deleteFilm(film.getId());
        return createFilm(film);
    }

    @Override
    public Optional<Film> deleteFilm(long id) {
        Film deletedFilm = null;
        Iterator<Film> filmIterator = films.iterator();
        while (filmIterator.hasNext()) {
            Film nextFilm = filmIterator.next();
            if (nextFilm.getId() == id) {
                deletedFilm = nextFilm;
                filmIterator.remove();
            }
        }
        return Optional.ofNullable(deletedFilm);
    }

    @Override
    public List<Film> getAllFilms() {
        return films;
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        return null;
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        return films.stream()
                .filter(film -> film.getId() == filmId)
                .findFirst();
    }

    public Optional<Film> getFilmByName(String filmName) {
        return getAllFilms().stream()
                .filter(f -> f.getName().equals(filmName))
                .findFirst();
    }

    public List<Film> getTopFilms(int count) {
        return getAllFilms().stream()
                .sorted((f0, f1) -> Integer.compare(f1.getLikes().size(), f0.getLikes().size()))
                .limit(count).collect(Collectors.toList());
    }

    public List<Genre> getAllGenres() {
        throw new UnsupportedOperationException("Functionality not implemented");
    }

    public List<Mpa> getAllMpa() {
        throw new UnsupportedOperationException("Functionality not implemented");
    }

    public Mpa getMpaById(long id) {
        throw new UnsupportedOperationException("Functionality not implemented");
    }

    public Genre getGenreById(long id) {
        throw new UnsupportedOperationException("Functionality not implemented");
    }

    public boolean isFilmExist(Long filmId) {
        throw new UnsupportedOperationException("Functionality not implemented");
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        throw new UnsupportedOperationException("Functionality not implemented");
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        throw new UnsupportedOperationException("Functionality not implemented");

    }
}
