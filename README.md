# java-filmorate

## ER-диаграмма базы DB Filmorate

![Filmorate DB ER-chart](/Filmorate%20DB_Kryuchkov.png)

### Примеры запросов к базе:

  **1. Получение всех фильмов:**
  
        SELECT * FROM films

  **2. Получение всех пользователей:**
  
       SELECT * FROM users

  **3. Получение 10 самых популярных фильмов:**
  
       SELECT
          fl.film_id as film_id,
          fl.name as name,
          COUNT(uf.user_id) as cnt
       FROM films fl
       INNER JOIN user_films uf ON uf.film_id = fl.film_id 
       GROUP BY
          film_id,
          name
       ORDER BY cnt DESC
       LIMIT 10

  **4. Список общих ПОДТВЕРЖДЕННЫХ друзей:**
  
      SELECT
         uf1.friend_id
      FROM user_friends uf1
      WHERE uf1.user_id = 1 -- ID первого пользователя
      AND uf1.friendship_status_id = 1 -- 1 означает подтвержденный статус 
      AND uf1.friend_id IN (
         SELECT friend_id
         FROM user_friends uf2
         WHERE uf2.user_id = 2 -- ID второго пользователя
         uf2.friendship_status_id = 1 -- 1 означает подтвержденный статус 
       )
       
    
