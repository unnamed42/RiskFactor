package com.tjh.riskfactor.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

import javax.persistence.*

@Entity @Table(name = "users")
class User(
    @Column(unique = true, nullable = false)
    var username: String,

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String,

    var email: String? = null,

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var isAdmin: Boolean = false,

    @Transient
    @JsonProperty(value = "group", access = JsonProperty.Access.WRITE_ONLY)
    var groupName: String = ""
): IEntity() {
    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    lateinit var group: Group
}
