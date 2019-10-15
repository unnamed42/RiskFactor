package com.tjh.riskfactor.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.util.Set;

@Data @Entity
@Table(name = "group")
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Group {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private Set<User> members;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_admins",
        joinColumns = @JoinColumn(name = "admin", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "gid", referencedColumnName = "id")
    )
    @JsonIgnore
    @ToString.Exclude
    private Set<User> admins;

}
