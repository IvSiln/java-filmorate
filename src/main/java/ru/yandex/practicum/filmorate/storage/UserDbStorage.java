package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.util.*;

@Repository
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User makeUser(SqlRowSet rs) throws EmptyResultDataAccessException {
        try {
            return new User(
                    rs.getLong("user_id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    Objects.requireNonNull(rs.getDate("birthday")).toLocalDate(),
                    getUserFriends(rs.getLong("user_id"))
            );
        } catch (DataAccessException e) {
            // Пробрасывание исключения на уровень вызывающего метода с сообщением об ошибке
            throw new EmptyResultDataAccessException("Ошибка при создании объекта User", 1, e);
        }
    }

    @Override
    public User createUser(User user) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("email", user.getEmail());
        parameters.put("login", user.getLogin());
        parameters.put("name", user.getName());
        SimpleJdbcInsert insertIntoUser;
        parameters.put("birthday", user.getBirthday());
        insertIntoUser = new SimpleJdbcInsert(this.jdbcTemplate).withTableName("users").usingGeneratedKeyColumns("user_id");
        Long userId = (Long) insertIntoUser.executeAndReturnKey(parameters);
        return getUserById(userId);
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update users set email=?, login=?, name=?, birthday=? where user_id=?";
        if (jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()) > 0) {
            return user;
        }
        return null;
    }

    @Override
    public User deleteUser(User deleteUser) {
        User user = getUserById(deleteUser.getId());
        if (user != null) {
            String sqlQuery = "DELETE FROM users WHERE user_id = ?";
            int numberOfRowAffected = jdbcTemplate.update(sqlQuery, deleteUser.getId());
            return numberOfRowAffected > 0 ? user : null;
        }
        return null;
    }

    public Optional<User> getUserByEmail(String email) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where email = ?", email);
        if (userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("user_id"), userRows.getString("name"));
            return Optional.of(makeUser(userRows));
        } else {
            log.info("Пользователь с email {} не найден.", email);
            return Optional.empty();
        }
    }

    public User deleteUserById(Long id) {
        User user = getUserById(id);
        if (user != null && jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id) > 0) {
            return user;
        }
        return null;
    }


    public List<Long> getUserFriends(Long userId) {
        List<Long> userFriends = new ArrayList<>();
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("select * from userfriends where user_id = ?", userId);
        while (friendsRows.next()) {
            userFriends.add(friendsRows.getLong("friend_id"));
        }
        return userFriends;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new User(
                                rs.getLong("user_id"),
                                rs.getString("email"),
                                rs.getString("login"),
                                rs.getString("name"),
                                rs.getDate("birthday").toLocalDate(),
                                getUserFriends(rs.getLong("user_id"))));
    }

    public boolean addFriend(long userId, long friendId) {
        String sqlQuery = "insert into userfriends (user_id, friend_id, friendship_confirm) " +
                "values (?, ?, ?)";
        return jdbcTemplate.update(sqlQuery,
                userId,
                friendId,
                "FALSE") > 0;
    }

    public boolean deleteFriend(long userId, long friendId) {
        String sqlQuery = "DELETE FROM userfriends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sqlQuery, userId, friendId) == 1;
    }

    public boolean isFriend(long userId, long friendId) {
        String sqlQuery = "SELECT count(*) FROM userfriends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId, friendId) > 0;
    }

    public int confirmFriendShip(long userId, int friendId) {
        String sqlQuery = "UPDATE USERFRIENDS SET friendship_confirm=? WHERE USER_ID=? AND FRIEND_ID=? ";
        int numberOfRowsAffected = jdbcTemplate.update(sqlQuery,
                "TRUE",
                userId,
                friendId);
        return numberOfRowsAffected;
    }

    public List<User> getAllFriends(long userId) {
        String sql = "SELECT * FROM users WHERE user_id IN ( SELECT DISTINCT friend_id FROM userfriends WHERE user_id = ? )";
        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new User(
                                rs.getLong("user_id"),
                                rs.getString("email"),
                                rs.getString("login"),
                                rs.getString("name"),
                                rs.getDate("birthday").toLocalDate(),
                                getUserFriends(rs.getLong("user_id"))),
                userId
        );
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> commonFriends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID IN (SELECT FRIEND_ID FROM USERFRIENDS WHERE  USER_ID=? AND  FRIEND_ID IN (SELECT FRIEND_ID FROM USERFRIENDS WHERE USER_ID=?))";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) ->
                        new User(
                                rs.getLong("user_id"),
                                rs.getString("email"),
                                rs.getString("login"),
                                rs.getString("name"),
                                rs.getDate("birthday").toLocalDate(),
                                getUserFriends(rs.getLong("user_id"))),
                id, otherId);
    }

    public boolean isUserExist(Long userId) {
        String sqlQuery = "SELECT 1 FROM USERS WHERE user_id=?";
        return Boolean.TRUE.equals(jdbcTemplate.query(sqlQuery,
                (ResultSet rs) -> {
                    return rs.next();
                }, userId
        ));
    }

    @Override
    public User getUserById(long id) {
        String sqlQuery = "SELECT * FROM USERS WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("user_id"), userRows.getString("name"));
            return makeUser(userRows);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
    }
}
