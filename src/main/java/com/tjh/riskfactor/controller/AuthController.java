package com.tjh.riskfactor.controller;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.util.JsonBuilder;
import com.tjh.riskfactor.entity.json.Login;
import com.tjh.riskfactor.security.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider provider;

    private String authenticate(String username, String password) {
        try {
            val authToken = new UsernamePasswordAuthenticationToken(username, password);
            return provider.generateToken(authManager.authenticate(authToken));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    String.format("user [%s] is disabled", username));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "wrong username or password");
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }

    @PostMapping
    String requestToken(@RequestBody Login json) {
        String username = json.getUsername(), password = json.getPassword();
//        users.ensureUserExists(username);
        val token = authenticate(username, password);
        return new JsonBuilder().add("username", username)
                    .add("token", token).build();
    }

    @GetMapping
    String tokenInfo(HttpServletRequest request) {
        // the token is validated before here, no need for invalidity report
        return provider.resolveToken(request).map(provider::tokenToJson).get();
    }

}
