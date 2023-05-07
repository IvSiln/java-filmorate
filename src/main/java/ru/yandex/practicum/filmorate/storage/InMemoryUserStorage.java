package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users;

    public InMemoryUserStorage() {
        users = new TreeMap<>();
    }

    public Map<Long, User> getUsers() {
        return users;
    }

    public User getUserById(long id) {
        return getUsers().get(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return getAllUsers().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.remove(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User deleteUser(User user) { // Пока удаление пользователя не предусмотрено в ТЗ
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            userList.add(entry.getValue());
        }
        return userList;
    }

    public List<User> getAllFriends(long userId) {
        List<User> allFriends = new ArrayList<>();
        for (Long id : getUserById(userId).getFriends()) {
            allFriends.add(getUserById(id));
        }
        return allFriends;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        List<User> commonFriends = new ArrayList<>();
        List<User> userFriends = getAllFriends(id);
        List<User> otherUserFriends = getAllFriends(otherId);
        if (commonFriends != null && otherUserFriends != null) {
            commonFriends = userFriends.stream()
                    .filter(otherUserFriends::contains)
                    .collect(Collectors.toList());
        }
        return commonFriends;
    }
}
