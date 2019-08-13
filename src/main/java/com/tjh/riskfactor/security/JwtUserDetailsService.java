package com.tjh.riskfactor.security;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tjh.riskfactor.enums.UserRole;
import com.tjh.riskfactor.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val user = repo.findByUsername(username).orElseThrow(() -> {
            val message = String.format("username [%s] not found", username);
            return new UsernameNotFoundException(message);
        });
        val authorities = UserRole.getAuthorities();
        return User.builder().username(user.getUsername())
                .password(user.getPassword())
                .accountLocked(user.disabled())
                .authorities(authorities).build();
    }
}
