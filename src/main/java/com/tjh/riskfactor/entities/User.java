package com.tjh.riskfactor.entities;

import lombok.Data;
import lombok.Getter;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    public void setRole(String role) {
        this.role = Role.valueOf(role);
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }

    public boolean disabled() {
        return status == Status.DISABLED;
    }

}
