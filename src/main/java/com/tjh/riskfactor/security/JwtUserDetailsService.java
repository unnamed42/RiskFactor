package com.tjh.riskfactor.security;

import lombok.val;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService users;

    /**
     * This component has to be initialized lazily because we have a dependency loop:
     *      JwtUserDetailsService -> UserService -> PasswordEncoder
     *      -> SecurityConfig -> JwtUserDetailsService
     * Once the loop is resolved, this constructor can be made an auto-generated one.
     */
    @Autowired
    public JwtUserDetailsService(@Lazy UserService users) {
        this.users = users;
    }

    private Collection<? extends GrantedAuthority> toAuthorities(Collection<Group> groups) {
        return groups.stream().map(Group::getName)
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        val user = users.getUser(username);
        val authorities = toAuthorities(user.getGroups());
        return User.builder().username(user.getUsername())
                .password(user.getPassword())
                .accountLocked(user.disabled())
                .authorities(authorities).build();
    }
}
