package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.service.UserService;

import java.util.Map;

import static com.tjh.riskfactor.util.Utils.require;

/**
 * 用户相关操作，比如设置用户信息，检查用户名重名等
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    /**
     * 检查用户名{@code username}是否存在
     * @param username 用户名
     * @return 存在时应答OK，不存在时应答NOT_FOUND
     */
    @RequestMapping(path = "/username/{username}", method = RequestMethod.HEAD)
    public ResponseEntity usernameExists(@PathVariable String username) {
        return new ResponseEntity(service.hasUsername(username) ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

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
    @PutMapping("/user/{id}")
    @PreAuthorize("@e.writeUserPermitted(#id)")
    public void updateInfo(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        final var username = require(body, "username");
    }

}
