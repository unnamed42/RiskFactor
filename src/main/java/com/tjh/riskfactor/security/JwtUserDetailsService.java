package com.tjh.riskfactor.security;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.repo.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    private Collection<? extends GrantedAuthority> toAuthorities(Collection<Group> groups) {
        return groups.stream().map(Group::getName)
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        val user = users.findByUsername(username).orElseThrow(() -> {
            val message = String.format("user [%s] not found", username);
            return new UsernameNotFoundException(message);
        });
        val authorities = toAuthorities(user.getGroups());
        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .accountExpired(false)
                .accountLocked(user.disabled())
                .authorities(authorities).build();
    }

}
