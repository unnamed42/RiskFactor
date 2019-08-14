package com.tjh.riskfactor.debug;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.repo.GroupRepository;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${debug}")
    private boolean debug;
    private boolean done = false;

    private final UserRepository users;
    private final GroupRepository groups;
    private final PasswordEncoder encoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!debug || done)
            return;
        val nobody = makeGroup("nobody");
        val root = makeGroup("root");
        val users = makeGroup("users");

        makeUser("admin", root);
        makeUser("user", users);
        makeUser("banned", nobody);
        done = true;
    }

    @Transactional
    void makeUser(String username, Group ...groups) {
        if(users.existsByUsername(username))
            return;
        users.save(new User().setUsername(username)
            .setPassword(encoder.encode(username))
            .setEmail(username + "@example.com")
            .setGroups(Arrays.asList(groups))
        );
    }

    @Transactional
    Group makeGroup(String name) {
        return groups.findByName(name).orElseGet(() ->
                groups.save(new Group().setName(name)));
    }

}
