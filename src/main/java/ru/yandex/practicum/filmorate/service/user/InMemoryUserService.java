package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Override
    public User addFriend(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        return friend;
    }

    @Override
    public User removeFriend(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        user.removeFriend(friendId);
        friend.removeFriend(userId);
        return user;
    }

    @Override
    public List<User> getUserFriends(long userId) {
        Set<Long> userFriendsId = userStorage.getUser(userId).getFriends();
        List<User> userFriends = new ArrayList<>();
        userFriendsId.forEach(t -> userFriends.add(userStorage.getUser(t)));
        return userFriends;
    }

    @Override
    public List<User> getUsersCommonFriends(long userId, long otherUserId) {
        Set<Long> commonFriendsIds;
        commonFriendsIds = userStorage.getUser(userId)
                                      .getFriends()
                                      .stream()
                                      .filter(p2 -> userStorage.getUser(otherUserId)
                                                               .getFriends()
                                                               .contains(p2))
                                      .collect(Collectors.toSet());
        List<User> commonFriends = new ArrayList<>();
        commonFriendsIds.forEach(t -> commonFriends.add(userStorage.getUser(t)));
        return commonFriends;
    }
}
