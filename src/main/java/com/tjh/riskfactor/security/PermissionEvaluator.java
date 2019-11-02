package com.tjh.riskfactor.security;

import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class PermissionEvaluator {

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
    public boolean writeUserPermitted(Integer userId) {
        var curr = me();
        // 自己可以更改
        if(curr.getId().equals(userId))
            return true;
        // root成员可以更改
        if(curr.isRoot())
            return true;
        // 组管理员可以更改
        return false;
    }

}
