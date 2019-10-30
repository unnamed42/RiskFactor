package com.tjh.riskfactor.security;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.service.AccountService;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final AccountService service;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        val user = service.findUserByName(username).orElseThrow(() -> {
            val message = String.format("user [%s] not found", username);
            return new UsernameNotFoundException(message);
        });

        val builder = User.withUsername(username).password(user.getPassword())
            .accountExpired(false);

        service.findGroupNameByMemberName(username).ifPresent(name -> {
            builder.accountLocked(name.equals("nobody")).authorities(name);
        });

        return builder.build();
    }

}
