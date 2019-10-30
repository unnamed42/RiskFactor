package com.tjh.riskfactor.security;

import lombok.val;

import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tjh.riskfactor.util.Utils;

import java.security.Principal;

@Component
public class PermissionEvaluator {

    private static Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean isRoot() {
        return Utils.isRoot(authentication());
    }

    public boolean canManage(String username) {
        val user = (Principal)authentication().getPrincipal();
        if(user.getName().equals(username))
            return true;
        return isRoot();
    }

}
