package com.tjh.riskfactor.entity.acl;

import lombok.Data;

import javax.persistence.*;

@Data @Entity
@Table(name = "acl_entry",
    uniqueConstraints = @UniqueConstraint(columnNames = {"acl_object_identity", "ace_order"})
)
public class AclEntry {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "acl_object_identity", nullable = false)
    private AclObjectIdentity aclObjectIdentity;

    @Column(name = "ace_order", nullable = false)
    private int aceOrder;

    @ManyToOne
    @JoinColumn(name = "sid", nullable = false)
    private AclSid sid;

    @Column(nullable = false)
    private int mask;

    @Column(nullable = false)
    private boolean granting;

    @Column(name = "audit_success", nullable = false)
    private boolean auditSuccess;

    @Column(name = "audit_failure", nullable = false)
    private boolean auditFailure;

}
