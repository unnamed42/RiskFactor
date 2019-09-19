package com.tjh.riskfactor.entity.acl;

import lombok.Data;

import javax.persistence.*;

@Data @Entity
@Table(name = "acl_object_identity",
    uniqueConstraints = @UniqueConstraint(columnNames = {"object_id_class", "object_id_identity"})
)
public class AclObjectIdentity {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "object_id_class", nullable = false)
    private AclClass objectIdClass;

    @Column(name = "object_id_identity", nullable = false)
    private Long objectIdIdentity;

    @ManyToOne
    @JoinColumn(name = "parent_object")
    private AclObjectIdentity parentObject;

    @ManyToOne
    @JoinColumn(name = "owner_sid")
    private AclSid ownerSid;

    @Column(name = "entries_inheriting", nullable = false)
    private boolean entriesInheriting;

}
