package com.tjh.riskfactor.service;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.entity.*;
import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.repo.SaveGuardRepository;
import com.tjh.riskfactor.util.ThrowingRunnable;
import static com.tjh.riskfactor.util.Utils.readTreeAsType;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.util.*;
import java.io.IOException;
import java.util.function.Function;
import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class DataService {

    private final TaskService tasks;
    private final UserService users;
    private final GroupService groups;

    private final SaveGuardRepository guards;

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Transactional
    public void reloadTask() throws IOException {
        tasks.drop();
        guards.deleteById(1);
        guarded(this::loadTasks, 1);
    }

    @Transactional
    public void reloadUsers() throws IOException {
        groups.drop(); users.drop();
        guards.deleteById(0);
        guarded(this::loadUsers, 0);
    }

    @Transactional
    public void init() throws Exception {
        guarded(this::loadUsers, 0);
        guarded(this::loadTasks, 1);
    }

    private void loadTasks() throws IOException {
        final var type = new TypeReference<Task>() {};
        try(final var is = TypeReference.class.getResourceAsStream("/data/task.yml")) {
            final var task = mapper.readValue(is, type);

            final var group = groups.groupWithName(task.getCenter())
                     .orElseThrow(() -> notFound("group", task.getCenter()));
            final var sections = this.tasks.saveSections(task.getSections().stream().map(this::prepareSection));
            this.tasks.saveTask(task.setGroup(group).setSections(sections));
        }
    }

    private void loadUsers() throws IOException {
        final var userListType = new TypeReference<List<User>>() {};
        final var groupListType = new TypeReference<List<Group>>() {};

        try(final var is = ObjectMapper.class.getResourceAsStream("/data/user.yml")) {
            final var node = mapper.readTree(is); assert node.isObject();
            final var root = (ObjectNode)node;

            List<User> userList = readTreeAsType(mapper, root.get("users"), userListType);
            List<Group> groupList = readTreeAsType(mapper, root.get("groups"), groupListType);

            final var map = this.users.saveAll(userList.stream().map(this.users::encoded)).stream()
                      .collect(toMap(User::getUsername, u -> u));

            Function<List<String>, Set<User>> cvt =
                list -> list != null ? list.stream().map(map::get).collect(toSet()) : null;

            this.groups.saveAll(groupList.stream().map(group ->
                group.setMembers(cvt.apply(group.getMemberNames()))
                    .setAdmins(cvt.apply(group.getAdminNames()))
            ));
        }
    }

    private Question prepareQuestion(Question q) {
        if(q.getList() != null) {
            final var questions = this.tasks.saveQuestions(
                q.getList().stream().peek(this::prepareQuestion));
            return q.setList(questions);
        }
        return q;
    }

    private Section prepareSection(Section section) {
        if(section.getSections() != null) {
            final var sections = this.tasks.saveSections(
                section.getSections().stream().map(this::prepareSection));
            section.setSections(sections);
        }
        if(section.getQuestions() != null) {
            final var questions = this.tasks.saveQuestions(
                section.getQuestions().stream().map(this::prepareQuestion));
            section.setQuestions(questions);
        }
        return section;
    }

    private <E extends Throwable> void guarded(ThrowingRunnable<E> runnable, int id) throws E {
        if(guards.existsById(id))
            return;
        runnable.run();
        guards.insert(id);
    }

}
