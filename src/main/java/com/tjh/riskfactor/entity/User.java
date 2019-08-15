package com.tjh.riskfactor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.util.Collection;

@Data @Entity
@Table(name = "users")
@Accessors(chain = true)
public class User {

    @Id @GeneratedValue
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String email;

    @ManyToMany
    @JoinTable(name = "group_members",
            joinColumns = { @JoinColumn(name = "uid", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "gid", referencedColumnName = "id") }
    )
    private Collection<Group> groups;

    public boolean disabled() {
        return groups.stream().anyMatch(group -> group.getName().equals("nobody"));
    }

}
