package com.rain.deepmock.data;

import com.rain.deepmock.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class UserRepo {
    private final Schedulers schedulers;
    private final UserMapper userMapper;
    private final UserStore userStore;

    public UserRepo(Schedulers schedulers, UserMapper userMapper, UserStore userStore) {
        this.schedulers = schedulers;
        this.userMapper = userMapper;
        this.userStore = userStore;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        for (UserEntity entity : userStore.getUsers()) {
            users.add(userMapper.toModel(entity));
        }
        return users;
    }
}
