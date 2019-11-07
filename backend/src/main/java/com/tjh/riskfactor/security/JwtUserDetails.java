package com.tjh.riskfactor.security;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.tjh.riskfactor.entity.User;

import java.util.Collection;
import java.util.Collections;

public class JwtUserDetails implements UserDetails {

    private User user;
    private Collection<SimpleGrantedAuthority> authorities;

    public JwtUserDetails(User entity, String groupName) {
        user = entity;
        authorities = Collections.singletonList(new SimpleGrantedAuthority(groupName));
    }

    public Integer getId() {
        return user.getId();
    }

    public boolean isRoot() {
        return isInGroup("root");
    }

    public boolean isGroupAdmin() {
        return user.isAdmin();
    }

    private boolean isInGroup(String name) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(name));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isInGroup("nobody");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
