package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.security.JwtTokenProvider;
import static com.tjh.riskfactor.util.Utils.kvMap;
import static com.tjh.riskfactor.util.Utils.require;

import java.util.Map;

/**
 * 负责用户登录信息的Controller
 */
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider provider;

    private Authentication authenticate(String username, String password) {
        try {
            final var authToken = new UsernamePasswordAuthenticationToken(username, password);
            return authManager.authenticate(authToken);
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

    /**
     * 请求登录，返回JWT。请求JSON格式为：
     * {
     *     "username": [username],
     *     "password": [password]
     * }
     * 给予应答内容的JSON格式为：
     * {
     *     "token": [token]
     * }
     * 不需要包含多余信息，因为token中已经编码了用户名和用户id
     *
     * @param body 请求体，格式如上
     * @return token应答，格式如上
     */
    @PostMapping("/login")
    public Map<String, Object> requestToken(@RequestBody Map<String, String> body) {
        final var username = require(body, "username", String.class);
        final var password = require(body, "password", String.class);

        final var auth = authenticate(username, password);
        final var token = provider.generateToken(auth);

        return kvMap("token", token).build();
    }

}
