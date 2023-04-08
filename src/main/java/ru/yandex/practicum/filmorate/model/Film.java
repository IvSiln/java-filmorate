package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validator.MinDateRelease;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class Film {
    long id;
    @NotBlank String name;
    @NotBlank @Size(min = 1, max = 200) String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @MinDateRelease
    @Past(message = "Не верная дате выхода фильма") LocalDate releaseDate;
    @NotNull @Positive
    @EqualsAndHashCode.Exclude
    int duration;
    @EqualsAndHashCode.Exclude
    Set<Long> likes;
}