package com.tjh.riskfactor.security;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class ACLPermissionEvaluator implements PermissionEvaluator {

    private static boolean isRoot(Authentication auth) {
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("root"));
    }

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if(auth == null || targetDomainObject == null || !(permission instanceof String))
            return false;
        if(isRoot(auth)) return true;
        // some other
        return false;
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        if(auth == null || targetType == null || !(permission instanceof String))
            return false;
        if(isRoot(auth)) return true;
        // some other
        return false;
    }

}
