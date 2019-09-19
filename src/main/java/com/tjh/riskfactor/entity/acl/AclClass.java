package com.tjh.riskfactor.entity.acl;

import lombok.Data;

import javax.persistence.*;

@Data @Entity
@Table(name = "acl_class")
public class AclClass {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "class", nullable = false, unique = true,
            columnDefinition = "varchar(100)")
    private String class_;

}
