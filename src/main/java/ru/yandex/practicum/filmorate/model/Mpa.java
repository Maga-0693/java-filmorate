package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
public class Mpa {
    private int id;
    private String name;
    private String description;

    public Mpa(int mpa_id, String mpa_name, String description) {
        this.id = mpa_id;
        this.name = mpa_name;
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
