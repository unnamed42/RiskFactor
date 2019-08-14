package com.tjh.riskfactor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.util.Collection;

@Entity @Data
@Table(name = "user")
@Accessors(chain = true)
public class User {

    @Id @GeneratedValue
    private Integer id;
    private String username;
    @JsonIgnore
    private String password;
    private String email;

    @ManyToMany
    @JoinTable(name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Collection<Role> roles;

    public boolean disabled() {
        return roles.stream().anyMatch(role -> role.getName().equals("ROLE_FROZEN"));
    }

}
