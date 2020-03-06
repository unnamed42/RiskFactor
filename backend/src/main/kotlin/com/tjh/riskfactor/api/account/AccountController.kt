package com.tjh.riskfactor.api.account

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.common.*

@CrossOrigin
@RestController
class AccountController(
    private val service: AccountService,
    private val encoder: PasswordEncoder
) {

    /**
     * 检查用户名[username]是否存在
     * @param username 用户名
     * @return 存在时应答OK，不存在时应答NOT_FOUND
     */
    @RequestMapping(path = ["/users/{username}"], method = [RequestMethod.HEAD])
    fun usernameExists(@PathVariable username: String) = service.hasUser(username).to404()

    /**
     * 更新用户信息。请求格式如下：
     * ```
     * {
     *     "username": [新用户名], (可选)
     *     "password": [新密码], (可选)
     * }
     * ```
     * 只有用户自己、root组成员、用户所属组的管理员可以更改
     * @param targetId 要更新的用户id
     * @param body 请求内容
     */
    @PutMapping("/users/{targetId}")
    @PreAuthorize("@e.canWriteUser(#targetId)")
    fun updateInfo(@PathVariable targetId: Int, @RequestBody body: UserInfo?) {
        if(body == null) return
        service.users.updateChecked(targetId) { user ->
            var updateCount = 0
            updateCount += body.username?.let { user.username = it } != null
            updateCount += body.password?.let { user.password = encoder.encode(it) } != null
            updateCount += body.email?.let { user.email = it } != null
            updateCount += body.isAdmin?.let { user.isAdmin = it } != null
            updateCount += body.group?.let { user.groupId = service.findGroup(it).id } != null
            updateCount != 0
        }
    }

    @PostMapping("/users")
    @PreAuthorize("@e.canAddUserTo(#body.group)")
    fun createUser(@RequestBody body: UserInfo) {
        val gid = body.group?.let { service.findGroup(it).id }
        if(body.password == null)
            throw invalidArg("cannot create user without password")
        val user = User(
            username = body.username,
            password = encoder.encode(body.password),
            email = body.email,
            isAdmin = body.isAdmin ?: false,
            groupId = gid ?: 0
        )
        service.users.save(user)
    }

    /**
     * 获得当前用户所能管理的全部用户的信息
     */
    @GetMapping("/users")
    fun userInfoList(@AuthenticationPrincipal details: AccountDetails): List<UserInfo> =
        service.visibleUserIds(details.id).let { service.userInfo(it) }

    /**
     * 获得指定用户的信息。特殊id 0代表自己
     */
    @GetMapping("/users/{targetId}")
    fun userInfo(@PathVariable targetId: Int, @AuthenticationPrincipal details: AccountDetails): UserInfo {
        service.users.mustHave(targetId)

        val userId = if(targetId == 0 || targetId == details.id) details.id else {
            if(!service.isTargetVisible(details.id, targetId))
                throw forbidden("no permission to investigate user $targetId")
            targetId
        }

        return service.userInfo(userId)
    }

}
