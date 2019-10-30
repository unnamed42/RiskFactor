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
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

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
        guards.deleteById(0);
        guarded(this::loadTasks, 0);
    }

    @Transactional
    public void reloadUsers() throws IOException {
        groups.drop(); users.drop();
        guards.deleteById(1);
        guarded(this::loadUsers, 1);
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

            this.groups.saveAll(groupList.stream().peek(group -> {
                group.setMembers(cvt.apply(group.getMemberNames()));
                group.setAdmins(cvt.apply(group.getAdminNames()));
            }));
        }
    }

    private Section prepareSection(Section section) {
        if(section.getSections() != null) {
            val sections = this.tasks.saveSections(
                section.getSections().stream().peek(this::prepareSection));
            return section.setSections(sections);
        }
        val questions = section.getQuestions().stream().map(q -> this.assignFields(q, "$"));
        return section.setQuestions(this.tasks.saveQuestions(questions));
    }

    private Question assignFields(Question q, String parent) {
        val key = assignKey(parent, q);
        // 递归设置子问题的缺失属性
        val list = q.getList();
        if(list != null && list.size() != 0)
            q.setList(tasks.saveQuestions(list.stream().map(q_ -> this.assignFields(q_, key))));
        return q;
    }

    // 沿问题路径生成唯一key
    private static String key(String parent, Question q) {
        if(parent == null)
            return null;
        val field = q.getField();
        if(field == null)
            // 类型为LIST的问题，本身在路径中无意义，作为包装存在，可以不参与生成
            // 对于这类Question，路径处理中跳过，即生成key为 [父问题field]/[LIST子问题field]
            return q.getType() == QuestionType.LIST ? parent : null;
        return String.format("%s/%s", parent, field);
    }

    private static String assignKey(String parent, Question q) {
        String key = key(parent, q);
        if(key != null && q.getType() != QuestionType.LIST)
            q.setField(key);
        return key;
    }

    private <E extends Throwable> void guarded(ThrowingRunnable<E> runnable, int id) throws E {
        if(guards.existsById(id))
            return;
        runnable.run();
        guards.insert(id);
    }

}
