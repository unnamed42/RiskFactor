package com.tjh.riskfactor.service;

import lombok.val;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.repo.GroupRepository;
import com.tjh.riskfactor.repo.SaveGuardRepository;
import com.tjh.riskfactor.repo.UserRepository;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class InitService {

    private final FormService forms;
    private final UserRepository users;
    private final GroupRepository groups;
    private final SaveGuardRepository guards;
    private final PasswordEncoder encoder;

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private void initForms() throws IOException {
        val mapper = new ObjectMapper(new YAMLFactory());
        val type = new TypeReference<List<Section>>() {};
        try(val is = TypeReference.class.getResourceAsStream("/data/sections.yml")) {
            List<Section> sections = mapper.readValue(is, type);
            sections.forEach(forms::saveSection);
        }
    }

    @Transactional
    void initAccounts() throws IOException {
        val mapper = new ObjectMapper(new YAMLFactory());
        val users = new HashMap<String, User>();

        Function<List<String>, Set<User>> cvt = list -> Optional.ofNullable(list).map(List::stream)
            .map(stream -> stream.map(users::get).collect(toSet()))
            .orElse(Collections.emptySet());

        try(val is = ObjectMapper.class.getResourceAsStream("/data/user.yml")) {
            val yaml = mapper.readValue(is, Yaml.class);
            for (val u : yaml.users) {
                val user = new User().setUsername(u.username)
                           .setPassword(encoder.encode(u.password)).setEmail(u.email);
                users.put(u.username, user);
            }
            Stream<Group> groups = yaml.groups.stream().map(g -> new Group().setName(g.name)
                .setMembers(cvt.apply(g.members))
                .setAdmins(cvt.apply(g.admins)));
            this.users.saveAll(users.values());
            this.groups.saveAll(groups::iterator);
        }
    }

    public void init() throws Exception {
        val runnable = new ThrowingRunnable[] {
            this::initForms, this::initAccounts
        };
        for(int i=0; i<runnable.length; ++i) {
            if(guards.existsById(i)) continue;
            runnable[i].run();
            guards.insert(i);
        }
    }

}

@Data class GroupYaml {
    String name;
    List<String> members;
    List<String> admins;
}
@Data class UserYaml {
    String username;
    String password;
    String email;
}
@Data class Yaml {
    List<GroupYaml> groups;
    List<UserYaml> users;
}
