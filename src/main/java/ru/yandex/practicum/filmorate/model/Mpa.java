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

    public Mpa(long mpa_id, String name) {
        this.id = mpa_id;
        this.name = name;
    }
}
