package com.tjh.riskfactor.api.account

import org.springframework.stereotype.Component
import org.springframework.security.core.context.SecurityContextHolder

import com.tjh.riskfactor.common.IdType
import com.tjh.riskfactor.common.findChecked
import com.tjh.riskfactor.api.answer.AnswerService

@Component("e")
class AccountPermissionEvaluator(
    private val accounts: AccountService,
    private val answers: AnswerService
) {

    private val auth get() = SecurityContextHolder.getContext().authentication
    private val self get() = (auth.principal as AccountDetails)

    /**
     * 检查当前登录用户能否更改指定用户的信息
     * @param uid 要更改信息的用户
     * @return 有权限更改与否
     */
    fun canWriteUser(uid: IdType): Boolean {
        // 请求一个不存在的用户暂且放行，详细错误放到controller中去
        return if(!accounts.users.existsById(uid))
            true
        else
            accounts.isTargetVisible(self.id, uid, true)
    }

    fun canAddUserTo(group: String?): Boolean {
        val self = this.self; val actor = self.dbUser
        val isRoot = actor.isRoot
        // 无权限组只有root组能修改
        if(group == null)
            return isRoot
        // root修改所有
        if(isRoot)
            return accounts.hasGroup(group)
        return actor.isAdmin && group == self.groupName
    }

    fun canWriteAnswer(answerId: IdType): Boolean {
        // 创建新id时
        if(answerId == 0)
            return true
        val actor = self.dbUser
        if(actor.isRoot)
            return true
        val answer = answers.answers.findChecked(answerId)
        return answer.creatorId == actor.id ||
            (actor.isAdmin && answer.groupId == actor.groupId)
    }

}
