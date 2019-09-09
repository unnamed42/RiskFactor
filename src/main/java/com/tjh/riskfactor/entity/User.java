package com.tjh.riskfactor.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import java.util.Set;

@Data @Entity
@Table(name = "users")
@NaturalIdCache
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private Integer id;

    @NaturalId
    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_members",
        joinColumns = { @JoinColumn(name = "uid", referencedColumnName = "id") },
        inverseJoinColumns = { @JoinColumn(name = "gid", referencedColumnName = "id") }
    )
    @ToString.Exclude
    private Set<Group> groups;

    public boolean disabled() {
        return groups.stream().anyMatch(group -> group.getName().equals("nobody"));
    }

}
