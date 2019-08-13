package com.tjh.riskfactor.json;

import lombok.Data;

import com.tjh.riskfactor.entity.User;

@Data
public class AddUserRequest {

    // username is contained in PathVariable
    // private final String username;
    private final String password;
    private final String role;
    private final String status;

    public User toUser(String username) {
        return new User().setUsername(username)
                .setPassword(password)
                .setRole(role)
                .setStatus(status);
    }

}
