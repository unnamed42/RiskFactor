package com.tjh.riskfactor.service;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.repo.GroupRepository;
import com.tjh.riskfactor.json.AddUserRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository users;
    private final GroupRepository groups;
    private final PasswordEncoder encoder;

    private static ResponseStatusException notFound(String field, String name) {
        val message = String.format("requested %s [%s] not found", field, name);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    public User getUser(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> notFound("user", username));
    }

    public void ensureUserExists(String username) {
        if(!users.existsByUsername(username))
            throw notFound("user", username);
    }

    public List<User> getAll() {
        return users.findAll();
    }

    public void deleteUser(String username) {
        users.deleteByUsername(username);
    }

    public void createUser(String username, AddUserRequest request) {
        val userGroups = Arrays.stream(request.getRole().split(","))
                .map(name -> groups.findByName(name).orElseThrow(() -> notFound("group", name)))
                .collect(Collectors.toList());
        val password = encoder.encode(request.getPassword());
        val user = new User().setUsername(username).setPassword(password)
                .setGroups(userGroups).setEmail(request.getEmail());
        users.save(user);
    }

    public void changePassword(String username, String password) {
        val user = getUser(username).setPassword(encoder.encode(password));
        users.save(user);
    }

}
