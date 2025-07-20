package ru.yandex.practicum.filmorate.model;

public class Mpa {
    private int id;
    private String name;
    private String description;

    public Mpa() {
        this.id = 0;
        this.name = "";
        this.description = "";
    }

    public Mpa(int id, String name, String description) {
        this.id = id;
        this.name = name;
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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
