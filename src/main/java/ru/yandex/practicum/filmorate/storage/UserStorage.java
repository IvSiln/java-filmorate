package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    User deleteUser(User deleteUser);

    List<User> getAllUsers();

    boolean isUserExist(Long userId);

    User getUserById(long id);

    Optional<User> getUserByEmail(String email);

    boolean isFriend(long userId, long friendId);

    boolean addFriend(long userId, long friendId);

    boolean deleteFriend(long userId, long friendId);

    List<User> getAllFriends(long userId);

    List<User> getCommonFriends(Long id, Long otherId);
}
