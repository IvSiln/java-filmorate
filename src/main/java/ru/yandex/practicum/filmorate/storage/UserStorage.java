package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    User deleteUser(long id);

    boolean isContains(long friendId);

    List<User> getCommonFriends(Long id, Long otherId);

    List<User> getAllFriends(Long userId);
}
