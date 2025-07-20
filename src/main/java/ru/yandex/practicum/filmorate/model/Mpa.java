package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
public class Mpa {
    private int id;
    private String name;
    private String description;

    public Mpa(int mpaId, String mpaName, String description) {
        this.id = mpaId;
        this.name = mpaName;
        this.description = description;
    }

    public Mpa(Integer id) {
        this.id = id;
        this.name = "";
        this.description = "";
    }

    public int getId() {

        return id;
    }

    public String getName() {

        return name;
    }
}
