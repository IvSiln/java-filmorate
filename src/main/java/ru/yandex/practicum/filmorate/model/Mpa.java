package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mpa {

    private long id;

    private String name;

    public Mpa() {
    }

    public Mpa(long mpaId, String name) {
        this.id = mpaId;
        this.name = name;
    }
}
