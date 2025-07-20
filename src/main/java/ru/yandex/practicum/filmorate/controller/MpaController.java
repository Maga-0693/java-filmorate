package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {

        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> getAllMpaRatings() {

        return mpaService.getAllMpa();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMpaById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(mpaService.getMpaById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
