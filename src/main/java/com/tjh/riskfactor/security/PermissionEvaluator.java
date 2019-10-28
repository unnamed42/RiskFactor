package com.tjh.riskfactor.security;

import lombok.val;

import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Component
public class PermissionEvaluator {

    private static Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static UserDetails principal() {
        val auth = authentication();
        if(!(auth instanceof UsernamePasswordAuthenticationToken))
            throw new RuntimeException("authentication actual type is not correct");
        return (UserDetails)(auth.getPrincipal());
    }

    public boolean isRoot() {
        return principal().getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("root"));
    }

    public boolean canManage(String username) {
        val user = principal();
        if(user.getUsername().equals(username))
            return true;
        return isRoot();
    }
}
