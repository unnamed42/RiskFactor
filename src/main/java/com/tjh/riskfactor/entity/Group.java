package com.tjh.riskfactor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

import java.util.Collection;

@Data @Entity
@Table(name = "groups")
@Accessors(chain = true)
public class Group {

    @Id @GeneratedValue
    private Integer id;
    private String name;

    @ManyToMany(mappedBy = "groups")
    private Collection<User> members;

}
