package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(long id) {
        if (userStorage.isContains(id)) {
            return userStorage.getUserById(id);
        }
        throw new NotFoundException("Пользователь не найден");
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addToFriends(long userId, long friendId) {
        isCheckFriend(userId, friendId);
        userStorage.getUserById(userId).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(userId);
    }

    public void deleteFromFriends(long userId, long friendId) {
        isCheckFriend(userId, friendId);
        userStorage.getUserById(userId).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(userId);
    }

    public List<User> getAllFriends(long userId) {
        return userStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        isCheckFriend(id, otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    private void isCheckFriend(Long userId, Long friendId) {
        if (!userStorage.isContains(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!userStorage.isContains(friendId)) {
            throw new NotFoundException("Друг не найден");
        }
    }
}
