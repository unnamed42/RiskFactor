package com.tjh.riskfactor.security;

import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

@Component
public class PermissionEvaluator {

    private static Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static JwtUserDetails principal() {
        return (JwtUserDetails) authentication().getPrincipal();
    }

    public boolean isRoot() {
        return principal().isRoot();
    }

    public boolean canManage(String username) {
        final var user = (Principal)authentication().getPrincipal();
        if(user.getName().equals(username))
            return true;
        return isRoot();
    }

}
