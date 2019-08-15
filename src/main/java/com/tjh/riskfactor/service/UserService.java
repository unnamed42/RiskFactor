package com.tjh.riskfactor.service;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.repo.GroupRepository;
import com.tjh.riskfactor.json.AddUserRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository users;
    private final GroupRepository groups;
    private final PasswordEncoder encoder;

    private static ResponseStatusException notFound(String field, String ...names) {
        val message = String.format("requested %s(s) [%s] not found", field, String.join(",", names));
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private static <T> Collection<T> findAllOf(Function<Collection<String>, Collection<T>> lookup,
                                               Collection<String> request, Function<T, String> getter,
                                               String fieldName) {
        val found = lookup.apply(request);
        val set = found.stream().map(getter).collect(Collectors.toSet());
        val missing = request.stream().filter(r -> !set.contains(r)).toArray(String[]::new);
        if(missing.length != 0)
            throw notFound(fieldName, missing);
        return found;
    }

    public User getUser(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> notFound("user", username));
    }

    public User getUser(Integer id) {
        return users.findById(id)
                .orElseThrow(() -> notFound("user id", id.toString()));
    }

    @Transactional(readOnly = true)
    public Collection<String> groupMembers(String group) {
        return groups.findByName(group).map(Group::getMembers)
                .orElseThrow(() -> notFound("group", group))
                .stream().map(User::getUsername).collect(Collectors.toList());
    }

    @Transactional
    public void appendMembers(String groupName, Collection<String> usernames) {
        val users = findAllOf(this.users::findByUsernameIn, usernames,
                              User::getUsername, "user");
        val group = groups.findByName(groupName)
                .orElseThrow(() -> notFound("group", groupName));
        group.getMembers().addAll(users);
        groups.save(group);
    }

    public void ensureUserExists(String username) {
        if(!users.existsByUsername(username))
            throw notFound("user", username);
    }

    public void deleteUser(String username) {
        users.deleteByUsername(username);
    }

    public void createUser(String username, AddUserRequest request) {
        if(users.existsByUsername(username))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("user [%s] already exists", username));
        // convert group String names to Group entities
        val groupNames = Optional.ofNullable(request.getGroups())
                .orElse(Collections.singletonList("users"));
        val userGroups = findAllOf(groups::findByNameIn, groupNames,
                                   Group::getName, "group");
        // create new user
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
