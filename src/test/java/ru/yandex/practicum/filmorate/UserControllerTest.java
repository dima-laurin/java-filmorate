package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private final UserController userController = new UserController();

    @Test
    void mustCreateUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userController.create(user);

        assertNotNull(created);
        assertNotNull(created.getId());
    }

    @Test
    void mustSetNameIfBlank() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userController.create(user);

        assertEquals("login", created.getName());
    }

    @Test
    void mustFailWhenLoginHasSpaces() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("bad login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void mustFailWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.update(user));
    }

    @Test
    void mustFailUpdateWhenIdNull() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.update(user));
    }
}