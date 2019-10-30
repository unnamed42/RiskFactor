package com.tjh.riskfactor.service;

import lombok.val;
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
import java.util.stream.Stream;
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
        val type = new TypeReference<Task>() {};
        try(val is = TypeReference.class.getResourceAsStream("/data/task.yml")) {
            val task = mapper.readValue(is, type);

            val group = groups.groupWithName(task.getCenter())
                     .orElseThrow(() -> notFound("group", task.getCenter()));
            val sections = this.tasks.saveSections(task.getSections().stream().map(this::prepareSection));
            this.tasks.saveTask(task.setGroup(group).setSections(sections));
        }
    }

    private void loadUsers() throws IOException {
        val userListType = new TypeReference<List<User>>() {};
        val groupListType = new TypeReference<List<Group>>() {};

        try(val is = ObjectMapper.class.getResourceAsStream("/data/user.yml")) {
            val node = mapper.readTree(is); assert node.isObject();
            val root = (ObjectNode)node;

            List<User> userList = readTreeAsType(mapper, root.get("users"), userListType);
            List<Group> groupList = readTreeAsType(mapper, root.get("groups"), groupListType);

            val map = this.users.saveAll(userList.stream().map(this.users::encoded)).stream()
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
            val questions = this.tasks.saveQuestions(
                q.getList().stream().peek(this::prepareQuestion));
            return q.setList(questions);
        }
        return q;
    }

    private Section prepareSection(Section section) {
        if(section.getSections() != null) {
            val sections = this.tasks.saveSections(
                section.getSections().stream().map(this::prepareSection));
            section.setSections(sections);
        }
        if(section.getQuestions() != null) {
            val questions = this.tasks.saveQuestions(
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
