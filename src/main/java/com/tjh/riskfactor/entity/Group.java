package com.tjh.riskfactor.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.util.Set;

@Data @Entity
@Table(name = "groups_") // group, groups为MySQL8保留字，都不能使用
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

}
