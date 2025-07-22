package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.api.MpaStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Mpa getMpaById(int id) {
        try {
            return mpaStorage.getMpaById(id)
                    .orElseThrow(() -> new NotFoundException("Mpa not exist by id=" + id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Mpa not exist by id=" + id);
        }
    }

    public List<Mpa> getAllMpa() {

        return mpaStorage.getAllMpa();
    }
}