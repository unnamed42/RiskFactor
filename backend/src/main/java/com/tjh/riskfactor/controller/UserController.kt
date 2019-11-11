package com.tjh.riskfactor.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize

import com.tjh.riskfactor.service.UserService

/**
 * 用户相关操作，比如设置用户信息，检查用户名重名等
 */
@CrossOrigin
@RestController
class UserController {

    @Autowired private lateinit var service: UserService

    /**
     * 检查用户名{@code username}是否存在
     * @param username 用户名
     * @return 存在时应答OK，不存在时应答NOT_FOUND
     */
    @RequestMapping(path = ["/username/{username}"], method = [RequestMethod.HEAD])
    fun usernameExists(@PathVariable username: String) =
        ResponseEntity<Any>(if(service.has(username)) HttpStatus.OK else HttpStatus.NOT_FOUND)

    /**
     * 更新用户信息。请求格式如下：
     * {
     *     "username": [新用户名], (可选)
     *     "password": [新密码], (可选)
     * }
     * 只有用户自己、root组成员、用户所属组的管理员可以更改
     * @param id 要更新的用户id
     * @param body 请求内容
     */
    @PutMapping("/users/{id}")
    @PreAuthorize("@e.canWriteUser(#id)")
    fun updateInfo(@PathVariable id: Int, @RequestBody body: Map<String, String>) {
        val user = service.checkedFind(id)
        if(body.isEmpty()) return
        body["username"]?.let { user.username = it }
        body["password"]?.let { user.password = service.encode(it) }
        service.save(user)
    }

}
