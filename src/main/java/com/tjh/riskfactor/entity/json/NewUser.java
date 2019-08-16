package com.tjh.riskfactor.entity.json;

import lombok.Data;

import java.util.Collection;

@Data
public class NewUser {

    // username is contained in PathVariable
    // private final String username;

    private final String password;
    private final String email;
    private final Collection<String> groups;

}
