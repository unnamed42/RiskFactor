package com.tjh.riskfactor.controller;

import lombok.val;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.util.JsonBuilder;
import com.tjh.riskfactor.json.AuthInfo;
import com.tjh.riskfactor.security.JwtTokenProvider;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repo;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider provider;

    @RequestMapping(method = RequestMethod.POST)
    String login(@RequestBody AuthInfo json) {
        String username = json.getUsername(), password = json.getPassword();
        val roles = repo.findByUsername(username).orElseThrow(() -> {
            val message = String.format("username [%s] not found", username);
            return new UsernameNotFoundException(message);
        }).getRoles();
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        val token = provider.generateToken(username, roles);
        return new JsonBuilder().add("username", username)
                    .add("token", token).build();
    }

//    @RequestMapping(method = RequestMethod.GET)
//    String currentUser(@AuthenticationPrincipal UserDetails details) {
//        val roles = details.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
//        return new JsonBuilder().add("username", details.getUsername())
//                .add("roles", roles).build();
//    }

}
