package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserDbService userService;

    @Autowired
    public UserController(UserDbService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        if (id <= 0) {
            throw new ValidationException("id должен быть больше 0");
        }
        return userService.getUserById(id);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        log.trace("Добавлен пользователь: " + createdUser);
        return createdUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updateUser) {
        User updatedUser = userService.updateUser(updateUser);
        log.trace("Изменен пользователь: " + updatedUser);
        return updateUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
        log.trace(String.format("Пользователь с ID: %d добавлен в друзья пользователя с ID: %d", friendId, id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
        log.trace(String.format("Пользователь с ID: %d удален из друзей пользователя с ID: %d", friendId, id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) {
        if (userService.getUserById(id) == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не существует.");
        }
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
