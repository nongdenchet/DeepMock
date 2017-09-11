package com.rain.deepmock.data;

import java.util.Arrays;
import java.util.List;

public class UserStore {
    public List<UserEntity> getUsers() {
        return Arrays.asList(
                new UserEntity("quan", "quan@gmail.com"),
                new UserEntity("rain", "rain@gmail.com")
        );
    }
}
