package com.tjh.riskfactor.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

import java.util.List;
import java.util.Set;

@Data @Entity
@Table(name = "group")
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class Group {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用于内部标识的用户组名，比如"root"
     */
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * 用于外部展示的用户组名，比如“华中科技大学”
     * 在本项目中也用作分中心名称
     */
    @Column(nullable = false)
    private String displayName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name = "group_members",
        joinColumns = @JoinColumn(name = "gid"),
        inverseJoinColumns = @JoinColumn(name = "uid")
    )
    @JsonIgnore
    private Set<User> members;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name = "group_admins",
        joinColumns = @JoinColumn(name = "admin"),
        inverseJoinColumns = @JoinColumn(name = "gid")
    )
    @JsonIgnore
    private Set<User> admins;

    @JsonProperty(value = "members", access = JsonProperty.Access.WRITE_ONLY)
    @Transient private List<String> memberNames;

    @JsonProperty(value = "admins", access = JsonProperty.Access.WRITE_ONLY)
    @Transient private List<String> adminNames;

}
