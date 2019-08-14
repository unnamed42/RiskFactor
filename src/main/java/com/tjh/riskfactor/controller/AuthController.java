package com.tjh.riskfactor.controller;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.util.JsonBuilder;
import com.tjh.riskfactor.json.AuthInfo;
import com.tjh.riskfactor.service.UserService;
import com.tjh.riskfactor.security.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService users;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider provider;

    @RequestMapping(method = RequestMethod.POST)
    String login(@RequestBody AuthInfo json) {
        String username = json.getUsername(), password = json.getPassword();
        users.ensureUserExists(username);
        val auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return new JsonBuilder().add("username", username)
                    .add("token", provider.generateToken(auth)).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    String info(HttpServletRequest request) {
        return provider.resolveToken(request).map(provider::tokenToJson).orElseThrow(() -> {
            val message = "request does not contain valid token";
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
        });
    }

}
