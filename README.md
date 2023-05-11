
# java-filmorate

Схема зависимостей filmorate.\
![Database schema](PUBLIC.png)

Примеры запросов:

1.список всех пользователей
SELECT *

FROM users

2.список всех фильмов
SELECT *

FROM films

3.пользователь с конкретным id
SELECT *

FROM users

WHERE user_id=?

4.фильм с конкретным id\
SELECT *

FROM films

WHERE film_id=?

5.получить информацию о пользователях и их друзьях\
SELECT  
u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY, f.FRIEND_ID

FROM
USERS u

    LEFT JOIN USERFRIENDS f ON u.USER_ID = f.USER_ID

ORDER BY
u.USER_ID;

6.получить список всех жанров и количество фильмов в каждом жанре \
SELECT
g.GENRE_ID, g.NAME AS GENRE_NAME,
COUNT(fg.FILM_ID) AS FILM_COUNT

FROM GENRES g

     LEFT JOIN FILMGENRES fg ON g.GENRE_ID = fg.GENRE_ID

GROUP BY
g.GENRE_ID, g.NAME

ORDER BY
g.GENRE_ID;

7.найти всех пользователей, которые имеют друзей и их текущий статус\
SELECT\
u.USER_ID, u.LOGIN, u.NAME, uf.FRIENDSHIP_CONFIRM AS FRIENDSHIP_STATUS\

FROM\
USERS u\
     
    INNER JOIN USERFRIENDS uf ON u.USER_ID = uf.USER_ID\

WHERE\
uf.FRIENDSHIP_CONFIRM = true;\

8.список общих друзей двух пользователей\
SELECT
u.USER_ID, u.LOGIN, u.NAME

FROM\
USERS u\
    
    INNER JOIN USERFRIENDS uf1 ON u.USER_ID = uf1.USER_ID\
    INNER JOIN USERFRIENDS uf2 ON uf1.FRIEND_ID = uf2.FRIEND_ID\
WHERE\
uf1.USER_ID = {user1_id} AND\
uf2.USER_ID = {user2_id} AND\
uf1.FRIENDSHIP_CONFIRM = true AND\
uf2.FRIENDSHIP_CONFIRM = true;
//user1_id и user2_id это id пользователей например 1 и 2
