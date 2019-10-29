package com.tjh.riskfactor.security;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.repo.GroupRepository;

@Service("userDetailsService")
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository users;
    private final GroupRepository groups;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        val user = users.findByUsername(username).orElseThrow(() -> {
            val message = String.format("user [%s] not found", username);
            return new UsernameNotFoundException(message);
        });

        val builder = User.withUsername(username).password(user.getPassword())
            .accountExpired(false);

        groups.findUserGroup(username).ifPresent(group -> {
            builder.accountLocked(group.getName().equals("nobody"))
                .authorities(group.getName());
        });

        return builder.build();
    }

}
