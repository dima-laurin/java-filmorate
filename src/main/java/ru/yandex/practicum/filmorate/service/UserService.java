package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Получение всех пользователей");
        return userStorage.findAll();
    }

    public User create(User user) {
        validateUser(user);
        fillUserNameIfBlank(user);

        User createdUser = userStorage.create(user);
        log.info("Пользователь создан: {}", createdUser);
        return createdUser;
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Не указан id");
        }

        validateUser(user);
        fillUserNameIfBlank(user);

        if (userStorage.getById(user.getId()) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        User updatedUser = userStorage.update(user);
        log.info("Пользователь обновлён: {}", updatedUser);
        return updatedUser;
    }

    public User getById(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Найден пользователь с id={}", id);
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Пользователи {} и {} добавлены в друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Пользователи {} и {} удалены из друзей", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = getById(userId);

        List<User> friends = userStorage.getByIds(user.getFriends());

        log.info("Получен список друзей пользователя id={}, количество={}", userId, friends.size());
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        getById(userId);
        getById(otherId);

        List<User> commonFriends = userStorage.getCommonFriends(userId, otherId);

        log.info("Получен список общих друзей пользователей {} и {}, количество={}",
                userId, otherId, commonFriends.size());
        return commonFriends;
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения в будущем");
        }
    }

    private void fillUserNameIfBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Не заполнено поле name, используется login");
        }
    }
}
