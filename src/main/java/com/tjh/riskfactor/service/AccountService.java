package com.tjh.riskfactor.service;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.repo.GroupRepository;
import com.tjh.riskfactor.entity.json.NewUser;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;
import static com.tjh.riskfactor.error.ResponseErrors.conflict;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 提供用户与组操作，暴露用户数据库和组数据库两个数据库的操作。
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository users;
    private final GroupRepository groups;
    private final PasswordEncoder encoder;

    /**
     * 查找所有位于 {@code request} 中的元素，要求所有元素全部存在
     * @param lookup 单个元素的查找函数
     * @param request 全部元素的id（不一定是主键）
     * @param getter 将元素转换成字符串，保证不同元素的转换结果不重复
     * @param fieldName 元素类型的名称，用于错误报告
     * @param <T> 请求元素的实际类型
     * @throws ResponseStatusException 当有不存在元素时抛出此异常
     * @return 找到的元素集合
     */
    private static <T> Set<T> findAllOf(Function<Collection<String>, Set<T>> lookup,
                                        Collection<String> request, Function<T, String> getter,
                                        String fieldName) {
        val found = lookup.apply(request);
        val set = found.stream().map(getter).collect(Collectors.toSet());
        val missing = request.stream().filter(r -> !set.contains(r)).toArray(String[]::new);
        if(missing.length != 0)
            throw notFound(fieldName, missing);
        return found;
    }

    ///
    /// Methods of groups
    ///

    private Set<Group> allGroupsIn(Collection<String> names) {
        return findAllOf(groups::findByNameIn, names,
                         Group::getName, "group");
    }

    public Collection<String> listGroups() {
        return groups.findAll().stream().map(Group::getName)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<String> listGroupMembers(String group) {
        return groups.findByName(group).map(Group::getMembers)
                .orElseThrow(() -> notFound("group", group))
                .stream().map(User::getUsername).collect(Collectors.toList());
    }

    public void createGroup(String groupName, Collection<String> userNames) {
        if(groups.existsByName(groupName))
            throw conflict("group", groupName);
        val users = Optional.ofNullable(userNames)
                        .map(this::allUsersIn)
                        .orElseGet(Collections::emptySet);
        groups.save(new Group().setName(groupName).setMembers(users));
    }

    @Transactional
    public void addGroupMembers(String groupName, Collection<String> userNames) {
        val users = this.allUsersIn(userNames);
        val group = groups.findByName(groupName)
                .orElseThrow(() -> notFound("group", groupName));
        group.getMembers().addAll(users);
        users.forEach(u -> u.getGroups().add(group));
        groups.save(group);
    }

    @Transactional
    public void deleteGroup(String groupName) {
        val found = groups.findByName(groupName);
        // do not report error for absent group
        if(!found.isPresent()) return;
        val group = found.get();
        for (val member : group.getMembers())
            member.getGroups().remove(group);
        groups.deleteById(group.getId());
    }

    ///
    /// Methods of users
    ///

    private Set<User> allUsersIn(Collection<String> userNames) {
        return findAllOf(users::findByUsernameIn, userNames,
                         User::getUsername, "user");
    }

    public User getUser(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> notFound("user", username));
    }

//    public User getUser(Integer id) {
//        return users.findById(id)
//                .orElseThrow(() -> notFound("user id", id.toString()));
//    }

//    public void ensureUserExists(String username) {
//        if(!users.existsByUsername(username))
//            throw notFound("user", username);
//    }

    public void deleteUser(String username) {
        users.deleteByUsername(username);
    }

    public void createUser(String username, NewUser request) {
        if(users.existsByUsername(username))
            throw conflict("user", username);
        val groupNames = Optional.ofNullable(request.getGroups())
                .orElse(Collections.singletonList("nobody"));
        // create new user
        users.save(new User().setGroups(allGroupsIn(groupNames))
             .setPassword(encoder.encode(request.getPassword()))
             .setUsername(username).setEmail(request.getEmail()));
    }

    public void changePassword(String username, String password) {
        val user = getUser(username).setPassword(encoder.encode(password));
        users.save(user);
    }

}
