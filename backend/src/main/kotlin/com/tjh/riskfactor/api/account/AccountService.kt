package com.tjh.riskfactor.api.account

import com.fasterxml.jackson.annotation.JsonProperty

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.crypto.password.PasswordEncoder

import com.tjh.riskfactor.common.*

import au.com.console.jpaspecificationdsl.*

@Service
class AccountService(
    val users: UserRepository,
    val groups: GroupRepository,
    private val encoder: PasswordEncoder
) {

    fun hasUser(name: String): Boolean = users.exists(User::username.equal(name))

    fun hasGroup(name: String): Boolean = groups.exists(Group::name.equal(name))

    fun findUserChecked(name: String): User =
        users.findOne(User::username.equal(name)).orElseThrow { users.notFound(name) }

    /**
     * 返回用户的用户名，不存在的情况下用其id代替
     */
    fun usernameOrId(userId: IdType): String {
        val user = users.findChecked(userId)
        return user.username ?: user.id.toString()
    }

    /**
     * 获得用以显示的组名。GID为0的组表示为无权限
     */
    fun groupName(groupId: IdType): String =
        if(groupId == 0) "无权限组" else groups.accessChecked(groupId) { it.name }

    private fun findGroupNullable(name: String): Group? =
        groups.findOne(Group::name.equal(name)).orElse(null)

    fun findGroup(name: String): Group =
        findGroupNullable(name) ?: throw groups.notFound(name)

    fun getGroupName(id: IdType): String? =
        if(id == 0) null else groups.accessChecked(id) { it.name }

    /**
     * 返回一个用户可以修改的其他用户id列表
     * @param actorId 以此id用户为视角观察
     */
    fun visibleUserIds(actorId: IdType): List<IdType> {
        val user = users.findChecked(actorId)
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

    /**
     * 确认[targetId]被[actorId]管理
     * @param requireWritable 是否还要要求[actorId]能够更改[targetId]的信息
     */
    fun isTargetVisible(actorId: IdType, targetId: IdType,
                        requireWritable: Boolean = false): Boolean {
        val user = users.findChecked(actorId)
        val target = users.findChecked(targetId)
        return user.isRoot || user.id == target.id ||
            ((!requireWritable || user.isAdmin) && user.groupId == target.groupId)
    }

    fun userInfo(id: IdType): UserInfo =
        users.findChecked(id).toUserInfo()

    fun userInfo(idList: Iterable<IdType>): List<UserInfo> =
        users.findAllById(idList).map { it.toUserInfo() }

    /**
     * 插入数据中指定的用户和用户组
     */
    @Transactional
    fun loadFromSchema(model: DataModel) {
        // 创建超级管理员组，确保其id为1
        val admin = findGroupNullable("超级管理员")
        if(admin != null && admin.id != 1)
            groups.deleteById(admin.id)
        if(!groups.existsById(1))
            groups.insert(1, "超级管理员")
        else
            groups.updateChecked(1) { it.name = "超级管理员"; true }

        val (groupList, userList) = model

        val groupRefs = mutableMapOf("nobody" to 0, "root" to 1)

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

    private fun User.toUserInfo(): UserInfo {
        val group = getGroupName(groupId)
        return UserInfo(username = username, email = email,
            group = group, isAdmin = isAdmin, password = null)
    }
}

data class UserInfo(
    val username: String?,
    val password: String?,
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
