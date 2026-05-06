package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        UserDbStorage.class,
        FilmDbStorage.class,
        GenreDbStorage.class,
        MpaDbStorage.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    @Test
    void testUserStorage() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userStorage.create(user);

        assertThat(createdUser.getId()).isNotNull();

        User foundUser = userStorage.getById(createdUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(createdUser.getId());
        assertThat(foundUser.getEmail()).isEqualTo("user@mail.ru");
        assertThat(foundUser.getLogin()).isEqualTo("user");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userStorage.create(user);

        createdUser.setEmail("new@mail.ru");
        createdUser.setLogin("newLogin");
        createdUser.setName("New Name");

        userStorage.update(createdUser);

        User updatedUser = userStorage.getById(createdUser.getId());

        assertThat(updatedUser.getEmail()).isEqualTo("new@mail.ru");
        assertThat(updatedUser.getLogin()).isEqualTo("newLogin");
        assertThat(updatedUser.getName()).isEqualTo("New Name");
    }

    @Test
    void testFriends() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User friend = new User();
        friend.setEmail("friend@mail.ru");
        friend.setLogin("friend");
        friend.setName("Friend");
        friend.setBirthday(LocalDate.of(2001, 1, 1));

        User createdUser = userStorage.create(user);
        User createdFriend = userStorage.create(friend);

        userStorage.addFriend(createdUser.getId(), createdFriend.getId());

        List<User> friends = userStorage.getFriends(createdUser.getId());

        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(createdFriend.getId());

        userStorage.removeFriend(createdUser.getId(), createdFriend.getId());

        List<User> friendsAfterRemove = userStorage.getFriends(createdUser.getId());

        assertThat(friendsAfterRemove).isEmpty();
    }

    @Test
    void testCommonFriends() {
        User firstUser = new User();
        firstUser.setEmail("first@mail.ru");
        firstUser.setLogin("first");
        firstUser.setName("First");
        firstUser.setBirthday(LocalDate.of(2000, 1, 1));

        User secondUser = new User();
        secondUser.setEmail("second@mail.ru");
        secondUser.setLogin("second");
        secondUser.setName("Second");
        secondUser.setBirthday(LocalDate.of(2000, 1, 1));

        User commonFriend = new User();
        commonFriend.setEmail("common@mail.ru");
        commonFriend.setLogin("common");
        commonFriend.setName("Common");
        commonFriend.setBirthday(LocalDate.of(2000, 1, 1));

        User createdFirstUser = userStorage.create(firstUser);
        User createdSecondUser = userStorage.create(secondUser);
        User createdCommonFriend = userStorage.create(commonFriend);

        userStorage.addFriend(createdFirstUser.getId(), createdCommonFriend.getId());
        userStorage.addFriend(createdSecondUser.getId(), createdCommonFriend.getId());

        List<User> commonFriends = userStorage.getCommonFriends(
                createdFirstUser.getId(),
                createdSecondUser.getId()
        );

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(createdCommonFriend.getId());
    }

    @Test
    void testMpaStorage() {
        List<Mpa> ratings = mpaStorage.getAll();

        assertThat(ratings).hasSize(5);

        Mpa mpa = mpaStorage.getById(1);

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1);
        assertThat(mpa.getName()).isEqualTo("G");
    }

    @Test
    void testGenreStorage() {
        List<Genre> genres = genreStorage.getAllGenres();

        assertThat(genres).hasSize(6);

        Genre genre = genreStorage.getGenreById(1);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    void testFilmStorage() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1);

        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        film.setGenres(genres);

        Film createdFilm = filmStorage.create(film);

        assertThat(createdFilm.getId()).isNotNull();

        Film foundFilm = filmStorage.getById(createdFilm.getId());

        assertThat(foundFilm).isNotNull();
        assertThat(foundFilm.getId()).isEqualTo(createdFilm.getId());
        assertThat(foundFilm.getName()).isEqualTo("Film");
        assertThat(foundFilm.getMpa().getId()).isEqualTo(1);
        assertThat(foundFilm.getMpa().getName()).isEqualTo("G");
        assertThat(foundFilm.getGenres()).hasSize(1);
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Old Film");
        film.setDescription("Old Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film createdFilm = filmStorage.create(film);

        createdFilm.setName("New Film");
        createdFilm.setDescription("New Description");
        createdFilm.setDuration(150);

        Mpa newMpa = new Mpa();
        newMpa.setId(2);
        createdFilm.setMpa(newMpa);

        filmStorage.update(createdFilm);

        Film updatedFilm = filmStorage.getById(createdFilm.getId());

        assertThat(updatedFilm.getName()).isEqualTo("New Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("New Description");
        assertThat(updatedFilm.getDuration()).isEqualTo(150);
        assertThat(updatedFilm.getMpa().getId()).isEqualTo(2);
        assertThat(updatedFilm.getMpa().getName()).isEqualTo("PG");
    }

    @Test
    void testLikesAndPopularFilms() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userStorage.create(user);

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film createdFilm = filmStorage.create(film);

        filmStorage.addLike(createdFilm.getId(), createdUser.getId());

        List<Film> popularFilms = filmStorage.getPopularFilms(10);

        assertThat(popularFilms).isNotEmpty();
        assertThat(popularFilms.get(0).getId()).isEqualTo(createdFilm.getId());

        filmStorage.removeLike(createdFilm.getId(), createdUser.getId());
    }
}
