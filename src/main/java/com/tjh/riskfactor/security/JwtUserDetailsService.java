package com.tjh.riskfactor.security;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.tjh.riskfactor.entity.Role;
import com.tjh.riskfactor.entity.Privilege;
import com.tjh.riskfactor.repo.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    private Collection<? extends GrantedAuthority> toAuthorities(Collection<Role> roles) {
        return roles.stream().map(Role::getPrivileges)
                .flatMap(Collection::stream).map(Privilege::getName)
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        val user = users.findByUsername(username).orElseThrow(() -> {
            val message = String.format("username [%s] not found", username);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        });
        val authorities = toAuthorities(user.getRoles());
        return User.builder().username(user.getUsername())
                .password(user.getPassword())
                .accountLocked(user.disabled())
                .authorities(authorities).build();
    }
}
