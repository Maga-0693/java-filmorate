package ru.yandex.practicum.filmorate.storage.api;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LikeStorage {

    void like(Integer filmId, Integer userId);

    ResponseEntity<String> unLike(Integer filmId, Integer userId);

    List<Integer> getLikesByFilmId(Integer id);
}