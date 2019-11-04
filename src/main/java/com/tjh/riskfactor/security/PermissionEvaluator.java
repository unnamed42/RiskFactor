package com.tjh.riskfactor.security;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tjh.riskfactor.service.UserService;

@Component
@RequiredArgsConstructor
public class PermissionEvaluator {

    private final UserService users;

    private static Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static JwtUserDetails me() {
        return (JwtUserDetails) authentication().getPrincipal();
    }

    public boolean isRoot() {
        return me().isRoot();
    }

    /**
     * 检查当前登录用户能否更改指定用户的信息
     * @param userId 要更改信息的用户
     * @return 有权限更改与否
     */
    public boolean canWriteUser(Integer userId) {
        // 请求一个不存在的用户暂且放行，详细错误放到controller中去
        if(!users.has(userId))
            return true;
        var curr = me();
        // 自己可以更改
        if(curr.getId().equals(userId))
            return true;
        // root成员可以更改
        if(curr.isRoot())
            return true;
        // 组管理员可以更改
        return users.isManaging(curr.getId(), userId);
    }

}
