package com.tjh.riskfactor.json;

import lombok.Data;

@Data
public class AddUserRequest {

    // username is contained in PathVariable
    // private final String username;
    private final String password;
    private final String role;
    private final String email;
    private final String nickname;

}
