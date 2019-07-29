package com.tjh.riskfactor.controllers;

import lombok.Data;
import lombok.val;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.tjh.riskfactor.entities.User;
import com.tjh.riskfactor.repos.UserRepository;
import com.tjh.riskfactor.utils.HttpUtils;
import com.tjh.riskfactor.utils.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Hide password from <c>User</c> entity.
     */
    @Data
    private static class UserJson {
        private String name;
        private String role;
        private String status;

        UserJson(User user) {
            this.name = user.getUsername();
            this.role = user.getRole().name();
            this.status = user.getStatus().name();
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    List<UserJson> getUsers() {
        return CollectionUtils.asStream(userRepository.findAll())
                .map(UserJson::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.GET)
    void getUser(@PathVariable String userName) {
        val user = HttpUtils.getById(userRepository, userName);
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    void deleteUser(@PathVariable String userName) {
        userRepository.deleteById(userName);
    }

    @RequestMapping(value = "/{userName}/password", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void changePassword(@PathVariable String userName, @RequestBody String password) {
        val user = HttpUtils.getById(userRepository, userName);
        user.setPassword(password);
        userRepository.save(user);
    }

}
