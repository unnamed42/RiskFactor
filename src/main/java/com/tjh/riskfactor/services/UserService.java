package com.tjh.riskfactor.services;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tjh.riskfactor.entities.User;
import com.tjh.riskfactor.repos.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    private User getOr404(String username) {
        return repo.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("user [%s] not found", username)));
    }

    public List<User> getUsers() {
        return repo.findAll();
    }

    public User getUser(String username) {
        return getOr404(username);
    }

    public void deleteUser(String username) {
        repo.deleteById(username);
    }

    public void addUser(User user) {
        if(repo.existsById(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("user [%s] already exists", user.getUsername()));
        repo.save(user);
    }

    public void changePassword(String username, String newPassword) {
        val user = getOr404(username);
        user.setPassword(newPassword);
        repo.save(user);
    }

}
