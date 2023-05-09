package ru.yandex.practicum.filmorate.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUserById(long id) {
        if (userStorage.getUserById(id) != null) {
            return userStorage.getUserById(id);
        } else {
            throw new NotFoundException("Не найден пользователь с ID: " + id);
        }
    }

    public Optional<User> getUserByEmail(String email) {
        return userStorage.getUserByEmail(email);
    }

    public User createUser(User user) {
        if (getUserByEmail(user.getEmail()).isPresent()) {
            throw new ValidationException("Пользователь с указанным адресом электронной почты уже был добавлен раннее");
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);

    }

    public User updateUser(User user) {
        if (getUserById(user.getId()) == null) {
            throw new NotFoundException("Нет пользователя с ID " + user.getId());
        }
        if (getUserByEmail(user.getEmail()).isPresent() && (getUserByEmail(user.getEmail()).get().getId() != user.getId())) {
            throw new ValidationException(String.format("Пользователю нельзя назначить такой email: %s, он уже используется.", user.getEmail()));
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        User updatedUser = userStorage.updateUser(user);
        return updatedUser;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public boolean addFriend(long userId, long friendId) {
        validateUsersExistence(userId, friendId);

        if (userStorage.isFriend(userId, friendId)) {
            throw new ValidationException(String.format("Пользователь с ID: %d уже является другом пользователю с ID: %d", friendId, userId));
        }
        userStorage.addFriend(userId, friendId);
        return true;
    }

    private void validateUsersExistence(long userId, long friendId) {
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);
        if (user1 == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не существует.");
        }
        if (user2 == null) {
            throw new NotFoundException("Пользователь с ID " + friendId + " не существует.");
        }
    }

    public boolean deleteFriend(long userId, long friendId) {
        validateUsersExistence(userId, friendId);
        if (!userStorage.isFriend(userId, friendId)) {
            throw new ValidationException(String.format("У пользователя ID: %d  нет друга с ID: %d", userId, friendId));
        }
        userStorage.deleteFriend(userId, friendId);
        return true;
    }


    public List<User> getAllFriends(Long userId) {
        if (getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не существует.");
        }
        return userStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        if (getUserById(id) == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не существует.");
        }
        if (getUserById(otherId) == null) {
            throw new NotFoundException("Пользователь с ID " + otherId + " не существует.");
        }
        return userStorage.getCommonFriends(id, otherId);
    }

    public boolean isUserExist(Long userId) {
        return userStorage.isUserExist(userId);
    }
}
