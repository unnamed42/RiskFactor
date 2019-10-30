package com.tjh.riskfactor.controller;

import lombok.val;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.security.JwtTokenProvider;

import static com.tjh.riskfactor.util.Utils.kvMap;
import static com.tjh.riskfactor.util.Utils.want;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider provider;

    private String authenticate(String username, String password) {
        try {
            val authToken = new UsernamePasswordAuthenticationToken(username, password);
            return provider.generateToken(authManager.authenticate(authToken));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "wrong password");
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());
        } catch (LockedException | DisabledException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getMessage());
        }
    }

    @PostMapping("/auth")
    String requestToken(@RequestBody Map<String, String> body) {
        val username = want(body, "username", String.class);
        val password = want(body, "password", String.class);

        val token = authenticate(username, password);

        return kvMap("username", username).add("token", token).buildJson().get();
    }

    @GetMapping("/auth")
    String tokenInfo(HttpServletRequest request) {
        // the token is validated before here, no need for invalidity report
        return provider.resolveToken(request).flatMap(provider::tokenToJson).get();
    }

}
