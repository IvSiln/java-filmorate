package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        deleteUser(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            userList.add(entry.getValue());
        }
        return userList;
    }

    @Override
    public User deleteUser(long id) {
        return users.remove(id);
    }

    @Override
    public boolean isContains(long friendId) {
        return users.containsKey(friendId);
    }

    @Override
    public List getCommonFriends(Long id, Long otherId) {
        List<User> commonFriends = new ArrayList<>();
        List<User> userFriends = getAllFriends(id);
        List<User> otherUserFriends = getAllFriends(otherId);
        if (otherUserFriends != null) {
            commonFriends = userFriends.stream().filter(otherUserFriends::contains).collect(Collectors.toList());
        }
        return commonFriends;
    }

    @Override
    public List<User> getAllFriends(Long userId) {
        List<User> userFriends = new ArrayList<>();
        if (getUserById(userId).getFriends() != null) {
            for (long id : getUserById(userId).getFriends()) {
                userFriends.add(getUserById(id));
            }
        }
        return userFriends;
    }
}
