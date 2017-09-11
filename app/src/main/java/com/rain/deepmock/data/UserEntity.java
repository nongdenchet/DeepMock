package com.rain.deepmock.data;

public class UserEntity {
    private final String username;
    private final String email;

    public UserEntity(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
