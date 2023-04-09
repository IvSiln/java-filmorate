package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private Long currentId;

    @Autowired
    public InMemoryUserStorage() {
        this.users = new HashMap<>();
        this.currentId = 0L;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (isValid(user)) {
            user.setId(++currentId);
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User update(User user) {
        check(user.getId());
        if (isValid(user)) {
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User getUserById(Long id) {
        check(id);
        return users.get(id);
    }

    @Override
    public User delete(Long userId) {
        check(userId);
        for (User user : users.values()) {
            user.getFriends().remove(userId);
        }
        return users.remove(userId);
    }

    private void check(Long userId) {
        if (userId == null) {
            throw new ValidationException("Пользователю не присвоен id");
        }
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
    }

    public boolean isValid(User user) {
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Неправильный e-mail пользователя: " + user.getEmail());
        }
        if ((user.getLogin().isEmpty()) || (user.getLogin().contains(" "))) {
            throw new ValidationException("Неправильный логин пользователя: " + user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Неправильная дата рождения пользователя: " + user.getBirthday());
        }
        return true;
    }
}
