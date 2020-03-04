package com.tjh.riskfactor.security

import org.springframework.stereotype.Component
import org.springframework.security.core.context.SecurityContextHolder

import com.tjh.riskfactor.repo.*

@Component
class PermissionEvaluator(private val users: UserRepository) {

    fun isRoot() = me().isRoot

    /**
     * 检查当前登录用户能否更改指定用户的信息
     * @param uid 要更改信息的用户
     * @return 有权限更改与否
     */
    fun canWriteUser(uid: Int): Boolean {
        // 请求一个不存在的用户暂且放行，详细错误放到controller中去
        if(!users.existsById(uid))
            return true
        val actor = me()
        // 自己可以更改
        if(actor.id == uid)
            return true
        // root成员可以更改
        if(actor.isRoot)
            return true
        // 组管理员可以更改
        return users.managedUserIds(actor.id).contains(uid)
    }

}

private fun authentication() = SecurityContextHolder.getContext().authentication
private fun me() = (authentication().principal as JwtUserDetails)
