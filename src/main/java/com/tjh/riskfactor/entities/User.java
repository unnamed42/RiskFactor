package com.tjh.riskfactor.entities;

import lombok.Data;
import lombok.Getter;
import lombok.AllArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Data
public class User {

    @AllArgsConstructor
    public enum Role {
        ADMIN(0), NORMAL(1);

        @Getter private int code;
    }

    @AllArgsConstructor
    public enum Status {
        ACTIVE(0), DISABLED(1);

        @Getter private int code;
    }

    private @Id String username;

    private String password;

    @Enumerated(EnumType.ORDINAL)
    private Role role;

    @Enumerated(EnumType.ORDINAL)
    private Status status;
}
