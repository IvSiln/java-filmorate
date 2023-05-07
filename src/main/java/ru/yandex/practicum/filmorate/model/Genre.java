package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Genre {

    private long id;

    private String name;

    public Genre(long genreId, String name) {
        this.id = genreId;
        this.name = name;
    }
}
