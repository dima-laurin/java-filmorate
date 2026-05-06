package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaStorage mpaStorage;

    public MpaController(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping
    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }

    @GetMapping("/{id}")
    public Mpa getById(@PathVariable int id) {
        Mpa mpa = mpaStorage.getById(id);

        if (mpa == null) {
            throw new NotFoundException("MPA не найден");
        }

        return mpa;
    }
}