package com.rain.deepmock.data;

public class UserMapper {
    public User toModel(UserEntity userEntity) {
        return new User(userEntity.getUsername(), userEntity.getEmail());
    }
}
