package com.tjh.riskfactor.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
    private val answers: AnswerRepository,
    private val schemas: SchemaRepository
) {

    fun hasUser(name: String): Boolean = users.exists(User::username.equal(name))

    fun hasGroup(name: String): Boolean = groups.exists(Group::name.equal(name))

    fun findUser(name: String): User =
        users.findOne(User::username.equal(name)).orElseThrow { users.notFound(name) }

    fun groupNames(): List<String> =
        groups.findAllNames()

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

    @Transactional
    fun deleteUser(id: IdType) {
        answers.markDeletedByCreator(id)
        schemas.markDeletedByCreator(id)
        users.deleteById(id)
    }

    /**
     * 返回一个用户可以修改的其他用户id列表
     * @param actorId 以此id用户为视角观察
     */
    @Transactional(readOnly = true)
    fun visibleUserIds(actorId: IdType): List<IdType> {
        val user = users.find(actorId)
        // 无权限组，只返回自己
        if(user.isNobody)
            return listOf(user.id)
        return when {
            // 超级管理员组返回全部用户
            user.isRoot -> users.findAllIds()
            // 组管理员返回全部组用户
            user.group != null && user.isAdmin -> users.findUserIdsInSameGroup(user.group!!)
            // 其余情况，返回自己
            else -> listOf(user.id)
        }
    }

    fun userInfo(id: IdType): UserInfo =
        userDetailedInfo(id).copy(isAdmin = null)

    fun userDetailedInfo(id: IdType): UserInfo =
        if(id != 0) users.find(id).toInfo() else
            UserInfo(id = 0, username = "[已删除]", email = null, group = "无权限", isAdmin = null)

    fun userDetailedInfo(idList: Iterable<IdType>): List<UserInfo> =
        users.findAllById(idList).map { it.toInfo() }

    private fun User.toInfo(): UserInfo {
        val group = this.group?.name
        return UserInfo(id = id, username = username, email = email,
            group = group, isAdmin = isAdmin)
    }
}

data class UserInfo(
    val id: IdType,
    val username: String?,
    val email: String?,
    val group: String?,
    val isAdmin: Boolean?
)
