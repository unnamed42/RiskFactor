package com.tjh.riskfactor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Collection;

@Data @Entity
@Table(name = "user_privileges")
@Accessors(chain = true)
public class Privilege {

    @Id @GeneratedValue
    private Integer id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;

}
