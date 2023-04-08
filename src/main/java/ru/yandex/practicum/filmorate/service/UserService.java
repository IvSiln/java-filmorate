package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;

public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User createUser(User user) {
        return inMemoryUserStorage.createUser(user);
    }

    public User updateUser(User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    public User getUserById(long id) {
        if (inMemoryUserStorage.isContains(id)) {
            return inMemoryUserStorage.getUserById(id);
        }
        throw new NotFoundException("Пользователь не найден");
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public void addToFriends(long userId, long friendId) {
        isCheckFriend(userId, friendId);
        inMemoryUserStorage.getUserById(userId).getFriends().add(friendId);
        inMemoryUserStorage.getUserById(friendId).getFriends().add(userId);
    }

    public void deleteFromFriends(long userId, long friendId) {
        isCheckFriend(userId, friendId);
        inMemoryUserStorage.getUserById(userId).getFriends().remove(friendId);
        inMemoryUserStorage.getUserById(friendId).getFriends().remove(userId);
    }

    public List<User> getAllFriends(long userId) {
        return inMemoryUserStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        isCheckFriend(id, otherId);
        return inMemoryUserStorage.getCommonFriends(id, otherId);
    }

    private void isCheckFriend(Long userId, Long friendId) {
        if (!inMemoryUserStorage.isContains(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!inMemoryUserStorage.isContains(friendId)) {
            throw new NotFoundException("Друг не найден");
        }
    }
}

