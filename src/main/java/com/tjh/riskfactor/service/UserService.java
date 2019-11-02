package com.tjh.riskfactor.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.repo.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IDBService<User> {

    @Getter
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public boolean hasUsername(String username) {
        return repo.existsByUsername(username);
    }

    public Optional<String> myGroupName(Integer uid) {
        return repo.findGroupName(uid);
    }

    public Optional<Integer> managedGroupId(Integer uid) {
        return repo.findManagedGroupId(uid);
    }

    public Optional<User> userWithName(String username) {
        return repo.findByUsername(username);
    }

    public String encode(String password) {
        return encoder.encode(password);
    }

    User encoded(User u) {
        return u.setPassword(encoder.encode(u.getPassword()));
    }

    @Override
    public String getEntityName() {
        return "user";
    }
}
