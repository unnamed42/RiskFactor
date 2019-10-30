package com.tjh.riskfactor.security;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.service.GroupService;
import com.tjh.riskfactor.service.UserService;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService users;
    private final GroupService groups;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        val user = users.userWithName(username).orElseThrow(() -> {
            val message = String.format("user [%s] not found", username);
            return new UsernameNotFoundException(message);
        });

        val builder = User.withUsername(username).password(user.getPassword())
            .accountExpired(false);

        groups.groupNameWithMember(username).ifPresent(name -> {
            builder.accountLocked(name.equals("nobody")).authorities(name);
        });

        return builder.build();
    }

}
