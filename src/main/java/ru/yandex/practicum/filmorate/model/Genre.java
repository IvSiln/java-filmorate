package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Genre {

    private long id;

    private String name;

    public Genre(long genre_id, String name) {
        this.id= genre_id;
        this.name= name;
    }
}
