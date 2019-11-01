package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.service.UserService;
import com.tjh.riskfactor.security.PermissionEvaluator;
import static com.tjh.riskfactor.util.Utils.optional;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;
import static com.tjh.riskfactor.error.ResponseErrors.forbidden;

import java.util.Map;

/**
 * 用户相关操作，比如设置用户信息，检查用户名重名等
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final PermissionEvaluator permission;

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
    public void updateInfo(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        final var user = service.user(id).orElseThrow(() -> notFound("user", id.toString()));
        if(!permission.writeUserPermitted(id))
            throw forbidden(String.format("not permitted to update user [%d]", id));
        if(body.size() == 0)
            return;
        optional(body, "username").ifPresent(user::setUsername);
        optional(body, "password").map(service::encodePassword).ifPresent(user::setPassword);
        service.save(user);
    }

}
