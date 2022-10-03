package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        if (user == null || friend == null) {
            throw new UserNotFoundException("Пользователь не найден!");
        }

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return friend;
    }

    public User deleteFriend(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        if (user == null || friend == null) {
            throw new UserNotFoundException("Пользователь не найден!");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return user;
    }

    public List<User> getUserFriends(long userId) {
        Set<Long> userFriendsId = userStorage.getUser(userId).getFriends();
        List<User> userFriends = new ArrayList<>();
        userFriendsId.forEach(t -> userFriends.add(userStorage.getUser(t)));
        return userFriends;
    }

    public List<User> getUsersCommonFriends(long userId, long otherUserId) {
        Set<Long> commonFriendsIds;
        if (userStorage.getUser(userId).getFriends() != null) {
            commonFriendsIds = userStorage.getUser(userId).getFriends().stream()
                    .filter(p2 -> userStorage.getUser(otherUserId).getFriends().contains(p2))
                    .collect(Collectors.toSet());
        } else {
            commonFriendsIds = Collections.EMPTY_SET;
        }
        List<User> commonFriends = new ArrayList<>();
        commonFriendsIds.forEach(t -> commonFriends.add(userStorage.getUser(t)));
        return commonFriends;
    }

}
