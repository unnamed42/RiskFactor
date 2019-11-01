package com.tjh.riskfactor.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.repo.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService implements IDBService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Override
    public void drop() {
        users.deleteAll();
    }

    public Optional<String> myGroupName(Integer uid) {
        return users.findGroupNameByUserId(uid);
    }

    public Optional<Integer> managedGroupId(Integer uid) {
        return users.findGroupIdManagedByUserId(uid);
    }

    public Optional<User> user(Integer id) {
        return users.findById(id);
    }

    public Optional<User> userWithName(String username) {
        return users.findByUsername(username);
    }

    public User encoded(User u) {
        return u.setPassword(encoder.encode(u.getPassword()));
    }

    List<User> saveAll(Stream<User> users) {
        return this.users.saveAll(users::iterator);
    }

}
