package com.tjh.riskfactor.component

import org.springframework.stereotype.Component
import org.springframework.security.core.context.SecurityContextHolder

import com.tjh.riskfactor.service.*
import com.tjh.riskfactor.repository.*

/**
 * 用于[org.springframework.security.access.prepost.PreAuthorize]的访问权限表达式。
 * 某些请求会特殊自行处理实体id不存在的情况，有些则没有，所以对不存在id的处理会有不同
 */
@Component("checker")
class PermissionEvaluator(
    private val accounts: AccountService,
    private val answers: AnswerService
) {

    private val auth get() = SecurityContextHolder.getContext().authentication
    private val self get() = (auth.principal as AccountDetails)
    private val currentUser get() = self.dbUser

    private fun isUserVisible(targetId: IdType, requireWritable: Boolean = false): Boolean {
        val current = currentUser
        if(targetId == current.id || targetId == 0)
            return true
        // 无权限组除了自己，什么也干不了
        if(current.isNobody)
            return false
        val targetUser = accounts.users.find(targetId)
        // 如果[targetUser]也是root组，那么规定root组的普通成员不能相互更改
        return (current.isRoot && (!requireWritable || !targetUser.isRoot)) ||
            // 只在要求可写时需要是组管理员
            ((!requireWritable || current.isAdmin) && current.groupId == targetUser.groupId)
    }

    fun isUserEnabled(): Boolean =
        !currentUser.isNobody

    /**
     * 是否可以修改用户[targetId]的信息
     */
    fun isUserWritable(targetId: IdType): Boolean =
        isUserVisible(targetId, true)

    fun isUserReadable(targetId: IdType): Boolean =
        isUserVisible(targetId)

    /**
     * 是否可以修改组名[groupName]的组的信息，即可添加或删除用户，修改组信息
     */
    fun isGroupWritable(groupName: String): Boolean =
        isGroupWritable(accounts.findGroup(groupName).id)

    fun isGroupWritable(targetId: IdType): Boolean {
        val current = currentUser
        return (current.isRoot && targetId != 1) ||
            current.groupId == targetId && current.isAdmin
    }

    fun isAnswerReadable(answerId: IdType): Boolean {
        val creatorId = answers.answers.propertyOf(answerId) { creatorId }
        return isUserVisible(creatorId)
    }

    /**
     * 是否可以更新回答[answerId]
     */
    fun isAnswerWritable(answerId: IdType): Boolean {
        val creatorId = answers.answers.propertyOf(answerId) { creatorId }
        return isUserVisible(creatorId, true)
    }

}
