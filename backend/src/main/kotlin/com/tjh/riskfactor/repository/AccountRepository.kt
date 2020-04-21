package com.tjh.riskfactor.repository

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query

import javax.persistence.*

@Entity @Table
class User(
    /**
     * 用户名，可为[null]。系统内部用的是用户id来区分，故用户名不重要
     */
    var username: String? = null,

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String = "",

    var email: String? = null,

    /**
     * 是否为组管理员
     */
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var isAdmin: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "gid", nullable = true)
    var group: Group?
): IEntity() {

    @get:JsonIgnore
    @get:Transient
    val isNobody get() = group?.id == 2

    @get:JsonIgnore
    @get:Transient
    val isRoot get() = group?.id == 1
}

@Entity @Table(name = "groups")
class Group(
    /**
     * 用于外部展示的用户组名，比如“华中科技大学”
     */
    @Column(nullable = false, unique = true)
    var name: String = ""
): IEntity()

@Repository
interface UserRepository: IQueryRepository<User, IdType> {
    @Query("select u.id from User u")
    fun findAllIds(): List<IdType>

    @Query("select u.id from User u where u.group = :group")
    fun findUserIdsInSameGroup(group: Group): List<IdType>
//
//    @Query("")
//    fun findVisibleUserIds(actorId: IdType): List<IdType>
}

@Repository
interface GroupRepository: IQueryRepository<Group, IdType> {
    @Query("select g.name from Group g")
    fun findAllNames(): List<String>
}
