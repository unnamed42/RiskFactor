package com.tjh.riskfactor.service;

import lombok.val;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.entity.form.Task;
import com.tjh.riskfactor.repo.SaveGuardRepository;

import java.util.*;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DataService {

    private final TaskService forms;
    private final AccountService accounts;

    private final SaveGuardRepository guards;

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private interface TRunnable<T extends Throwable> {
        void run() throws T;
    }

    private static <T> T readTreeAsValue(TreeNode node, TypeReference<T> type) throws IOException {
        return mapper.readValue(
            mapper.treeAsTokens(node),
            mapper.getTypeFactory().constructType(type)
        );
    }

    @Transactional
    void initForms() throws IOException {
        val type = new TypeReference<Task>() {};
        try(val is = TypeReference.class.getResourceAsStream("/data/sections.yml")) {
            val task = mapper.readValue(is, type);
            if(task.getMtime() == null)
                task.setMtime(new Date());
            this.forms.saveTask(task);
        }
    }

    @Transactional
    void initAccounts() throws IOException {
        val userListType = new TypeReference<List<User>>() {};
        val groupListType = new TypeReference<List<Group>>() {};

        try(val is = ObjectMapper.class.getResourceAsStream("/data/user.yml")) {
            val node = mapper.readTree(is); assert node.isObject();
            val root = (ObjectNode)node;

            val userList = readTreeAsValue(root.get("users"), userListType);
            val groupList = readTreeAsValue(root.get("groups"), groupListType);
            accounts.saveAll(userList, groupList);
        }
    }

    private <T extends Throwable> void guarded(TRunnable<T> runnable, int guard) throws T {
        if(guards.existsById(guard))
            return;
        runnable.run();
        guards.insert(guard);
    }

    @Transactional
    public void reloadForms() throws IOException {
        forms.drop();
        guards.deleteById(0);
        guarded(this::initForms, 0);
    }

    @Transactional
    public void reloadUsers() throws IOException {
        accounts.drop();
        guards.deleteById(1);
        guarded(this::initAccounts, 1);
    }

    public void init() throws Exception {
        guarded(this::initAccounts, 0);
        guarded(this::initForms, 1);
    }

}
