package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос списка пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        log.info("Создается пользователь {}", user);

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Нельзя использовать пробелы в логине");
        }

        user.setId(getNextId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Не заполнено поле name, используется login");
        }
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        log.info("Обновление пользователя {}", user);

        if (user.getId() == null) {
            throw new ValidationException("Не указан id");
        }

        if (!userExists(user.getId())) {
            throw new ValidationException("Пользователь не найден");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        return user;
    }

    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean userExists(Long id) {
        return users.containsKey(id);
    }
}

