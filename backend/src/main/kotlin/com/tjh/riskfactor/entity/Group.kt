package com.tjh.riskfactor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.*

@Entity @Table(name = "group")
class Group(
    /**
     * 用于内部标识的用户组名，比如"root"
     */
    @get:Column(unique = true, nullable = false)
    var name: String,

    /**
     * 用于外部展示的用户组名，比如“华中科技大学”
     * 在本项目中也用作分中心名称
     */
    @get:Column(nullable = false)
    var displayName: String,

    @get:OneToMany(mappedBy = "group")
    @JsonIgnore
    var members: MutableSet<User>
): IEntity()
