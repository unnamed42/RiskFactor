package com.tjh.riskfactor.services;

import lombok.val;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import com.tjh.riskfactor.repos.UserRepository;

@Service
public class AuthService {

    private final UserRepository repo;

    @Autowired
    public AuthService(UserRepository repo) {
        this.repo = repo;
    }

    public String getToken(String username, String password) {
        val user = repo.findById(username);
        val pass = user.map(value -> value.getPassword().equals(password))
                       .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                           String.format("user [%s] not found", username)));
        if(!pass)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("user [%s] authentication failed", username));
        return "";
    }

}
