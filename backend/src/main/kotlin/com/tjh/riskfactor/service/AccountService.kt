package com.tjh.riskfactor.service

import com.fasterxml.jackson.annotation.JsonProperty

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.crypto.password.PasswordEncoder

import au.com.console.jpaspecificationdsl.*
import com.tjh.riskfactor.repository.*

/**
 * 负责用户信息和组信息的管理。不能当作[org.springframework.security.core.userdetails.UserDetailsService]，
 * 是因为该Service Bean的创建依赖于UserDetailsService，如果实现该接口会导致Bean的循环依赖
 */
@Service
class AccountService(
    val users: UserRepository,
    val groups: GroupRepository,
    private val encoder: PasswordEncoder
) {

    fun hasUser(name: String): Boolean = users.exists(User::username.equal(name))

    fun hasGroup(name: String): Boolean = groups.exists(Group::name.equal(name))

    fun findUser(name: String): User =
        users.findOne(User::username.equal(name)).orElseThrow { users.notFound(name) }

    /**
     * 返回用户的用户名，不存在的情况下用其id代替
     */
    fun usernameOrId(userId: IdType): String {
        val user = users.find(userId)
        return user.username ?: user.id.toString()
    }

    fun findGroupUnchecked(name: String): Group? =
        groups.findOne(Group::name.equal(name)).orElse(null)

    fun findGroup(name: String): Group =
        findGroupUnchecked(name) ?: throw groups.notFound(name)

    fun findGroupName(id: IdType): String =
        groups.propertyOf(id) { name }

    /**
     * 返回一个用户可以修改的其他用户id列表
     * @param actorId 以此id用户为视角观察
     */
    fun visibleUserIds(actorId: IdType): List<IdType> {
        val user = users.find(actorId)
        // 无权限组，只返回自己
        if(user.isNobody)
            return listOf(user.id)
        val result: List<IdOnly>? = when {
            // 超级管理员组返回全部用户
            user.isRoot -> users.findAllProjected()
            // 组管理员返回全部组用户
            user.isAdmin -> users.findAllProjected(User::groupId.equal(user.groupId))
            // 其余情况，返回自己。用null标记
            else -> null
        }
        return result?.map { it.id } ?: listOf(user.id)
    }

    fun userInfo(id: IdType): UserInfo =
        users.find(id).toInfo().copy(isAdmin = null)

    fun userDetailedInfo(id: IdType): UserInfo =
        users.find(id).toInfo()

    fun userDetailedInfo(idList: Iterable<IdType>): List<UserInfo> =
        users.findAllById(idList).map { it.toInfo() }

    /**
     * 插入数据中指定的用户和用户组
     */
    @Transactional
    fun loadFromSchema(model: DataModel) {
        fun defineGroup(id: IdType, name: String) {
            val group = findGroupUnchecked(name)
            if(group != null && group.id != id)
                groups.deleteById(group.id)
            if(!groups.existsById(id))
                groups.insert(id, name)
            else
                groups.update(id) { this.name = name }
        }

        defineGroup(1, "超级管理员")
        defineGroup(2, "无权限")

        val (groupList, userList) = model

        val groupRefs = mutableMapOf("root" to 1, "nobody" to 2)

        // 存储groups，并将组名引用指向其id
        groupList.map { it.ref to groups.save(Group(it.name)).id }
            .toMap(groupRefs)
        // 存储users，并利用组名引用指向真实组id
        users.saveAll(userList.map { it.toUser(groupRefs) })
    }

    private fun UserModel.toUser(groupRefs: Map<String, IdType>) = User(
        username = username, password = encoder.encode(password),
        email = email, isAdmin = isAdmin,
        groupId = groupRefs[groupRef] ?: throw Exception("group $groupRef not found")
    )

    private fun User.toInfo(): UserInfo {
        val group = groups.propertyOf(groupId) { name }
        return UserInfo(username = username, email = email,
            group = group, isAdmin = isAdmin)
    }
}

data class UserInfo(
    val username: String?,
    val email: String?,
    val group: String?,
    val isAdmin: Boolean?
)

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
