package ru.yandex.practicum.filmorate.model;

import lombok.*;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.validation.MinReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    @MinReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    @NotNull(message = "MPA rating не может быть null")
    private Mpa mpa;

    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @Builder.Default
    private Set<Integer> likes = new HashSet<>();

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

    public LocalDate getReleaseDate() {

        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {

        this.releaseDate = releaseDate;
    }

    public int getDuration() {

        return duration;
    }

    public void setDuration(int duration) {

        this.duration = duration;
    }

    public Mpa getMpa() {

        return mpa;
    }

    public void setMpa(Mpa mpa) {

        this.mpa = mpa;
    }

    public Set<Genre> getGenres() {

        return genres;
    }

    public void setGenres(Set<Genre> genres) {

        this.genres = genres;
    }

    public Set<Integer> getLikes() {

        return likes;
    }

    public void setLikes(Set<Integer> likes) {

        this.likes = likes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Film film = (Film) o;

        if (id != film.id) return false;
        if (duration != film.duration) return false;
        if (!Objects.equals(name, film.name)) return false;
        if (!Objects.equals(description, film.description)) return false;
        if (!releaseDate.equals(film.releaseDate)) return false;
        if (!Objects.equals(mpa, film.mpa)) return false;
        if (!Objects.equals(genres, film.genres)) return false;
        return Objects.equals(likes, film.likes);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + releaseDate.hashCode();
        result = 31 * result + duration;
        result = 31 * result + (mpa != null ? mpa.hashCode() : 0);
        result = 31 * result + (genres != null ? genres.hashCode() : 0);
        result = 31 * result + (likes != null ? likes.hashCode() : 0);
        return result;
    }

    public Film() {
    }
}
