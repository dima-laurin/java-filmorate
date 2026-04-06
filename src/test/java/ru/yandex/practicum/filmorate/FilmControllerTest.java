package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private final FilmController filmController = new FilmController();

    @Test
    void mustCreateFilm() {
        Film film = new Film();
        film.setName("Star Wars");
        film.setDescription("Move about space");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());

        Film created = filmController.create(film);

        assertNotNull(created);
        assertNotNull(created.getId());
    }

    @Test
    void mustFailWhenDateIsTooEarly() {
        Film film = new Film();
        film.setName("Old film");
        film.setDescription("desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void mustUpdateFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());

        Film created = filmController.create(film);

        created.setName("Updated");

        Film updated = filmController.update(created);

        assertEquals("Updated", updated.getName());
    }

    @Test
    void mustFailUpdateWhenIdNull() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());

        assertThrows(ValidationException.class, () -> filmController.update(film));
    }

    @Test
    void mustFailUpdateWhenFilmNotExists() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Film");
        film.setDescription("desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());

        assertThrows(ValidationException.class, () -> filmController.update(film));
    }
}