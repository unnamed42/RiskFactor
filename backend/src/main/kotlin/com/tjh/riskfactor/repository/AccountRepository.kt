package com.tjh.riskfactor.repository

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying

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

    /**
     * 所属组的id。为0代表是不属于任何组，也就是没有任何权限
     */
    @Column(nullable = false)
    var groupId: IdType = 0
): IEntity() {

    @get:JsonIgnore
    @get:Transient
    val isNobody get() = groupId == 2

    @get:JsonIgnore
    @get:Transient
    val isRoot get() = groupId == 1
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
interface UserRepository: IQueryRepository<User, IdType>

@Repository
interface GroupRepository: IQueryRepository<Group, IdType> {
    @Modifying
    @Query(nativeQuery = true, value = "insert into `groups`(id, name) values (?1, ?2)")
    fun insert(id: IdType, name: String)
}
