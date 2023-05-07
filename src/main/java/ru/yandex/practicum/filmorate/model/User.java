package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.*;
import javax.validation.constraints.*;

@Getter
@Setter
public class User {

    private long id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @NotBlank
    @Pattern(regexp = "^[^\\s]*$")
    private String login;

    private String name;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Не верная дата рождения")
    private LocalDate birthday;

    private List<Long> friends;



    public User() {
        this.id = 0;
        this.friends = new ArrayList<>();
    }

    public User(long id, String email, String login, String name, LocalDate birthday, List<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
    }

    public List<Long> getFriends() {
        return friends;
    }

    public boolean addFriend(Long id) {
        return  friends.add(id);
    }

    public boolean deleteFriend(Long id) {
        return  friends.remove(id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
