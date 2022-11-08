package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserService {
    User addFriend(long userId, long friendId);

    User removeFriend(long userId, long friendId);

    List<User> getUserFriends(long userId);

    List<User> getUsersCommonFriends(long userId, long otherUserId);

}
