package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaStorage mpaStorage;

    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("Запрошен список всех MPA-рейтингов");
        return mpaStorage.getAllMpa();
    }

    @GetMapping("/{mpaId}")
    public Mpa getMpa(@PathVariable int mpaId) {
        log.info("Запрошен MPA-рейтинг с ID = {}", mpaId);
        Mpa mpa = mpaStorage.getMpaById(mpaId);

        if (mpa != null) {
            log.info("Найден MPA-рейтинг {} {}", mpaId, mpa.getName());
            return mpa;
        } else {
            log.warn("MPA-рейтинг с ID " + mpaId + " не найден");
            throw new MpaNotFoundException("MPA-рейтинг с ID " + mpaId + " не найден");
        }
    }
}
