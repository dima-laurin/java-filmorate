package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Slf4j
@Service
public class FilmService {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<Film> findAll() {
        log.info("Получение всех фильмов");
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validateFilm(film);
        validateMpaAndGenres(film);

        Film createdFilm = filmStorage.create(film);
        log.info("Фильм создан: {}", createdFilm);
        return createdFilm;
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Не указан id");
        }

        validateFilm(film);
        validateMpaAndGenres(film);

        if (filmStorage.getById(film.getId()) == null) {
            throw new NotFoundException("Фильм не найден");
        }

        Film updatedFilm = filmStorage.update(film);
        log.info("Фильм обновлён: {}", updatedFilm);
        return updatedFilm;
    }

    public Film getById(Long id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        log.info("Найден фильм с id={}", id);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        getById(filmId);

        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        getById(filmId);

        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        filmStorage.removeLike(filmId, userId);

        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.getPopularFilms(count);
        log.info("Получен список популярных фильмов, count={}", count);
        return popularFilms;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата слишком ранняя");
        }
    }

    private void validateMpaAndGenres(Film film) {
        Integer mpaId = film.getMpa().getId();

        if (mpaId == null || mpaStorage.getById(mpaId) == null) {
            throw new NotFoundException("MPA не найден");
        }

        if (film.getGenres() == null) {
            return;
        }

        for (Genre genre : film.getGenres()) {
            if (genre == null || genre.getId() <= 0 || genreStorage.getGenreById(genre.getId()) == null) {
                throw new NotFoundException("Жанр не найден");
            }
        }
    }
}
