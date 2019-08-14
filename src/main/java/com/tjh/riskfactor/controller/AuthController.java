package com.tjh.riskfactor.controller;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.util.JsonBuilder;
import com.tjh.riskfactor.json.AuthInfo;
import com.tjh.riskfactor.security.JwtTokenProvider;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository users;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider provider;

    @Value("${security.jwt.claimed-property}")
    private String claimedProperty;

    @RequestMapping(method = RequestMethod.POST)
    String login(@RequestBody AuthInfo json) {
        String username = json.getUsername(), password = json.getPassword();
        if(!users.existsByUsername(username)) {
            val message = String.format("username [%s] not found", username);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        val auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return new JsonBuilder().add("username", username)
                    .add("token", provider.generateToken(auth)).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    String info(@RequestHeader("Authorization") String bearer) {
        val claims = provider.resolveToken(bearer)
                .map(provider::parseClaims).orElseThrow(() -> {
            val message = "request does not contain valid token";
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
        });
        return new JsonBuilder().add("subject", claims.getSubject())
                .add("issued_at", claims.getIssuedAt())
                .add("expiry", claims.getExpiration())
                .add(claimedProperty, claims.get(claimedProperty))
                .build();
    }

}
