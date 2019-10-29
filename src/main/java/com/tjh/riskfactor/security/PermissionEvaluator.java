package com.tjh.riskfactor.security;

import lombok.val;

import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;

@Component
public class PermissionEvaluator {

    private static Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean isRoot() {
        return authentication().getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("root"));
    }

    public boolean canManage(String username) {
        val user = (Principal)authentication().getPrincipal();
        if(user.getName().equals(username))
            return true;
        return isRoot();
    }

}
