package com.tjh.riskfactor.security;

import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Component
public class PermissionEvaluator {

    public boolean isRoot(UserDetails user) {
        return user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("root"));
    }

}
