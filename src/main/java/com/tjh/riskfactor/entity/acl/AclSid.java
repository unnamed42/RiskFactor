package com.tjh.riskfactor.entity.acl;

import lombok.Data;

import javax.persistence.*;

@Data @Entity
@Table(name = "acl_sid",
    uniqueConstraints = @UniqueConstraint(columnNames = {"sid", "principal"})
)
public class AclSid {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "principal", nullable = false)
    private boolean principal;

    @Column(name = "sid", nullable = false, columnDefinition = "varchar(100)")
    private String sid;

}
