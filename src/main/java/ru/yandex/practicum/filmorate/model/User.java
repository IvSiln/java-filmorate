package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class User {
    Long id;
    @Email
    String email;
    @NotBlank
    @Pattern(regexp = "\\S*$")
    String login;
    @NotBlank
    @NonNull
    String name;
    @PastOrPresent
    LocalDate birthday;
    Set<Long> friends;

    public User(Long id, String email, String login, String name, LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        if ((name == null) || (name.isEmpty()) || (name.isBlank())) {
            this.name = login;
        }
        this.birthday = birthday;
        this.friends = friends;
        if (friends == null) {
            this.friends = new HashSet<>();
        }
    }

    public void setName(String name) {
        if ((name == null) || (name.isEmpty()) || (name.isBlank())) {
            this.name = login;
        }
    }
}
