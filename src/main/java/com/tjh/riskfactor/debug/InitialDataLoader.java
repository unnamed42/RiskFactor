package com.tjh.riskfactor.debug;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tjh.riskfactor.entity.Privilege;
import com.tjh.riskfactor.entity.Role;
import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.repo.PrivilegeRepository;
import com.tjh.riskfactor.repo.RoleRepository;
import com.tjh.riskfactor.repo.UserRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${debug}")
    private boolean debug;
    private boolean done = false;

    private final UserRepository users;
    private final RoleRepository roles;
    private final PrivilegeRepository privileges;
    private final PasswordEncoder encoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!debug || done)
            return;
        val read = createPrivilegeIfNotFound("READ");
        val write = createPrivilegeIfNotFound("WRITE");
        val adminRole = createRoleIfNotFound("ROLE_ADMIN", Arrays.asList(read, write));
        val userRole = createRoleIfNotFound("ROLE_USER", Collections.singletonList(read));
        createRoleIfNotFound("ROLE_FROZEN", Collections.emptyList());

        users.save(new User().setUsername("admin")
                .setPassword(encoder.encode("admin"))
                .setEmail("mail@example.com")
                .setRoles(Collections.singletonList(adminRole))
        );
        users.save(new User().setUsername("user")
                .setPassword(encoder.encode("user"))
                .setEmail("mail@example.com")
                .setRoles(Collections.singletonList(userRole))
        );
        done = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
        return privileges.findByName(name).orElseGet(() ->
                privileges.save(new Privilege().setName(name)));
    }

    @Transactional
    Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        return roles.findByName(name).orElseGet(() ->
                roles.save(new Role().setName(name).setPrivileges(privileges)));
    }

}
