package com.tjh.riskfactor.service;

import lombok.val;
import lombok.var;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.repo.GroupRepository;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;
import static com.tjh.riskfactor.error.ResponseErrors.conflict;
import static com.tjh.riskfactor.error.ResponseErrors.invalidArg;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

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
     * 根据可唯一区分实体的键（ID，用户名等等），查找实体，所有{@code request}中代表的实体都要存在
     * @param lookup 根据唯一区分键的集合查找所有实体的函数
     * @param request 唯一区分键的集合，需要无重复！
     * @param getter 获取实体内对应唯一区分键的函数
     * @param fieldName 实体名称，用于错误报告
     * @param <T> 请求元素的实际类型
     * @throws ResponseStatusException 当有不存在实体时抛出404异常
     * @return 找到的实体集合
     */
    private static <T> Set<T> findEvery(Function<Collection<String>, Set<T>> lookup,
                                        Collection<String> request, Function<T, String> getter,
                                        String fieldName) {
        val found = lookup.apply(request);
        val set = found.stream().map(getter).collect(toSet());
        val missing = request.stream().filter(r -> !set.contains(r)).toArray(String[]::new);
        if(missing.length != 0)
            throw notFound(fieldName, missing);
        return found;
    }

    ///
    /// Methods of groups
    ///

    /**
     * 根据名称查找到对应用户组，要求全部存在
     * @param names 全部用户组名称
     * @return 用户组数据库实体的集合
     */
    private Set<Group> namesToGroups(Collection<String> names) {
        return findEvery(groups::findByNameIn, names,
                         Group::getName, "group");
    }

    /**
     * 列出所有用户组的名称
     * @return 全部用户组名称
     */
    public Collection<String> listGroups() {
        return groups.getAllGroupNames();
    }

    /**
     * 列出组内的全部用户的用户名
     * @param group 用户组的名字
     * @throws ResponseStatusException 当{@code group}不存在的时候，抛出404异常
     * @return 组内用户的用户名
     */
    @Transactional(readOnly = true)
    public Collection<String> listMembers(String group) {
        return groups.findByName(group).map(Group::getMembers)
                .orElseThrow(() -> notFound("group", group))
                .stream().map(User::getUsername).collect(toList());
    }

    /**
     * 创建用户组
     * @param groupName 新用户组名称
     * @param userNames 组内成员的用户名，可以为{@code null}或者空
     */
    public Group createGroup(String groupName, Collection<String> userNames) {
        if(groups.existsByName(groupName))
            throw conflict("group", groupName);
        val users = Optional.ofNullable(userNames)
                        .map(this::namesToUsers)
                        .orElseGet(Collections::emptySet);
        return groups.save(new Group().setName(groupName).setMembers(users));
    }

    /**
     * 向用户组添加用户
     * @param groupName 用户组名称
     * @param userNames 要添加的用户名
     * @throws ResponseStatusException 当用户组{@code groupName}或者任一{@code userNames}中用户不存在时，抛出404异常
     */
    @Transactional
    public void addMembers(String groupName, Collection<String> userNames) {
        val users = this.namesToUsers(userNames);
        val group = groups.findByName(groupName)
                .orElseThrow(() -> notFound("group", groupName));
        group.getMembers().addAll(users);
        users.forEach(u -> u.getGroups().add(group));
        groups.save(group);
    }

    /**
     * 删除用户组
     * @param groupName 要删除的用户组名，可以不存在
     */
    @Transactional
    public void deleteGroup(String groupName) {
        val found = groups.findByName(groupName);
        // 不存在的用户组，啥也不干
        if(!found.isPresent())
            return;
        val group = found.get();
        // TODO: 需要确认这是否是正确的删除姿势
        for (val member : group.getMembers())
            member.getGroups().remove(group);
        groups.deleteById(group.getId());
    }

    ///
    /// Methods of users
    ///

    private Set<User> namesToUsers(Collection<String> userNames) {
        return findEvery(users::findByUsernameIn, userNames,
                         User::getUsername, "user");
    }

    public User getUser(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> notFound("user", username));
    }

    public void deleteUser(String username) {
        users.deleteByUsername(username);
    }

    /**
     * 新建用户。若不指定用户组，则是nobody组中的成员
     * @param user 用户名
     */
    @Transactional
    public User createUser(User user) {
        val username = user.getUsername();
        if(username == null)
            throw invalidArg("username", "null");
        if(users.existsByUsername(username))
            throw conflict("user", username);

        if(user.getPassword() == null)
            throw invalidArg("password", "null");
        user.setPassword(encoder.encode(user.getPassword()));

        var groups = user.getGroups();
        if(groups == null || groups.size() == 0) {
            val nobody = this.groups.findByName("nobody").get();
            groups = Collections.singleton(nobody);
        } else
            groups = namesToGroups(groups.stream().map(Group::getName).collect(toList()));
        user.setGroups(groups);

        return users.save(user);
    }

    public void changePassword(String username, String password) {
        val user = getUser(username).setPassword(encoder.encode(password));
        users.save(user);
    }

}
