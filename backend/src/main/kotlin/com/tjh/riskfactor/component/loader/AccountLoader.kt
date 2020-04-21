package com.tjh.riskfactor.component.loader

import com.fasterxml.jackson.annotation.JsonProperty

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.crypto.password.PasswordEncoder

import au.com.console.jpaspecificationdsl.equal

import com.tjh.riskfactor.repository.*

@Component
class AccountLoader(
    private val groups: GroupRepository,
    private val users: UserRepository,
    private val encoder: PasswordEncoder
) {

    private fun defineGroup(gid: IdType, name: String): Group {
        val group = groups.findOne(Group::name.equal(name)).orElse(null)
        if(group != null && group.id != gid)
            groups.deleteById(group.id)
        if(!groups.existsById(gid))
            return groups.save(Group(name = name).apply { id = gid })
        else
            return groups.update(gid) { this.name = name }
    }

    /**
     * 插入数据中指定的用户和用户组
     */
    @Transactional
    fun loadFromSchema(model: DataModel) {
        val root = defineGroup(1, "超级管理员")
        val nobody = defineGroup(2, "无权限")

        val (groupList, userList) = model

        val groupRefs = mutableMapOf("root" to root, "nobody" to nobody)

        // 存储groups，并将组名引用指向其id
        groupList.map { it.ref to groups.save(Group(it.name)) }.toMap(groupRefs)
        // 存储users，并利用组名引用指向真实组id
        users.saveAll(userList.map { User(
            username = it.username, password = encoder.encode(it.password),
            email = it.email, isAdmin = it.isAdmin,
            group = groupRefs[it.groupRef] ?: throw Exception("group ${it.groupRef} not found")
        ) })
    }
}

data class DataModel(
    val groups: List<GroupModel>,
    val users: List<UserModel>
)

data class GroupModel(
    val ref: String,
    val name: String
)

data class UserModel(
    val username: String,
    val password: String,
    val email: String?,
    @JsonProperty("group")
    val groupRef: String = "nobody",
    val isAdmin: Boolean = false
)
