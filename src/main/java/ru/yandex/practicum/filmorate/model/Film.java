package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validator.MinDateRelease;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class Film {
    Long id;

    @NotBlank String name;

    @NotBlank
    @Size(min = 1, max = 200)
    String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @MinDateRelease
    @Past(message = "Не верная дате выхода фильма")
    LocalDate releaseDate;

    @NotNull
    @Positive
    @EqualsAndHashCode.Exclude
    Integer duration;

    @EqualsAndHashCode.Exclude
    Set<Long> likes;

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, Set<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
        if (likes == null) {
            this.likes = new HashSet<>();
        }
    }
}