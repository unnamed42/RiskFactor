package com.tjh.riskfactor.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

import javax.persistence.*

@Entity @Table(name = "users")
class User(
    @get:Column(unique = true, nullable = false)
    var username: String,

    @get:Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String,

    @get:Column
    var email: String? = null,

    @get:Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var isAdmin: Boolean = false,

    @get:Transient
    @JsonProperty(value = "group", access = JsonProperty.Access.WRITE_ONLY)
    var groupName: String = ""
): IEntity() {
    @get:ManyToOne
    @get:JoinColumn(nullable = false)
    @JsonIgnore
    lateinit var group: Group
}
