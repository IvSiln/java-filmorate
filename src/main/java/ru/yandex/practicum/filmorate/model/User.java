package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
//какой-то произвольный текст, чтоб пул реквест прошел
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Integer id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past(message = "Не верная дата рождения")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;
}
