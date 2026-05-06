package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User user);

    User getById(Long id);

    List<User> getByIds(Collection<Long> ids);

    List<User> getCommonFriends(Long userId, Long otherId);
}
