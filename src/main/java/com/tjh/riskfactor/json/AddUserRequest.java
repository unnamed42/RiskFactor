package com.tjh.riskfactor.json;

import lombok.Data;

import java.util.Collection;

@Data
public class AddUserRequest {

    // username is contained in PathVariable
    // private final String username;

    private final String password;
    private final String email;
    private final Collection<String> groups;

}
