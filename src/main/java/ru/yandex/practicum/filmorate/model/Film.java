package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validator.MinDateRelease;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class Film {
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 1, max = 200)
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @MinDateRelease
    @Past(message = "Не верная дате выхода фильма")
    private LocalDate releaseDate;

    @NotNull
    @Positive
    private int duration;

    private long rate;

    @NotNull
    private Mpa mpa;

    private List<Genre> genres;

    private Set<Long> likes;

    public Film() {
        this.id = 0;
        this.likes = new HashSet<>();
        this.genres = new ArrayList<>();
    }

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, long rate, Mpa mpa, List<Genre> genres, Set<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
        this.genres = genres;
        this.likes = likes;
    }

    public boolean addLike(Long id) {
        return likes.add(id);
    }

    public boolean deleteLike(Long id) {
        return likes.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Film film = (Film) o;
        return id == film.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
