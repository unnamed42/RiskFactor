package com.tjh.riskfactor.services;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tjh.riskfactor.repos.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        val user = repo.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return User.withUsername(username).password(user.getPassword())
                .roles(user.getRole().toString())
                .disabled(user.disabled())
                .build();
    }

}
