package com.tjh.riskfactor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.util.List;
import java.util.Collections;

import com.tjh.riskfactor.enums.UserRole;
import com.tjh.riskfactor.enums.UserStatus;

@Entity @Data
@Table(name = "user")
@Accessors(chain = true)
public class User {

    @Id
    private String username;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public User setRole(String role) {
        this.role = UserRole.valueOf(role); return this;
    }

    public User setStatus(String status) {
        this.status = UserStatus.valueOf(status); return this;
    }

    public List<String> getRoles() {
        return Collections.singletonList(role.name());
    }

    public boolean disabled() {
        return status == UserStatus.DISABLED;
    }

}
