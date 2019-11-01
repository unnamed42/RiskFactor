package com.tjh.riskfactor.security;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tjh.riskfactor.service.UserService;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService users;

    @Override
    public UserDetails loadUserByUsername(String username) {
        final var user = users.userWithName(username).orElseThrow(() -> {
            final var message = String.format("user [%s] not found", username);
            return new UsernameNotFoundException(message);
        });
        final var groupName = users.myGroupName(user.getId()).orElse("nobody");
        return new JwtUserDetails(user, groupName);
    }

}
