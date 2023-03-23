package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {
    @NotNull
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    @NotBlank
    private String description;
    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
   private LocalDate releaseDate;
    @NotNull
    @Positive
    private Duration duration;
}
