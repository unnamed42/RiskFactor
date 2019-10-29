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

@Service("userDetailsService")
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String id) {
        val user = users.findById(Integer.valueOf(id)).orElseThrow(() -> {
            val message = String.format("user with id [%s] not found", id);
            return new UsernameNotFoundException(message);
        });

        val builder = User.withUsername(id).password(user.getPassword())
            .accountExpired(false);

        users.findGroupById(Integer.valueOf(id)).ifPresent(group -> {
            builder.accountLocked(group.getName().equals("nobody"))
                .authorities(group.getName());
        });

        return builder.build();
    }

}
