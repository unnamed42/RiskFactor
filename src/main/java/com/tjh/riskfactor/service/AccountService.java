package com.tjh.riskfactor.service;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.repo.GroupRepository;

import java.util.*;
import java.util.stream.Stream;
import java.util.function.Function;
import static java.util.stream.Collectors.*;

/**
 * 提供用户与组操作，暴露用户数据库和组数据库两个数据库的操作。
 */
@Service
@RequiredArgsConstructor
public class AccountService implements IDBService {

    private final UserRepository users;
    private final GroupRepository groups;
    private final PasswordEncoder encoder;

    @Transactional
    public void drop() {
        groups.deleteAll();
        users.deleteAll();
    }

    /**
     * 查找用户所管理的用户组
     * @param username 用户名
     * @return 该用户管理的用户组id，为空则代表用户并不是组管理员
     */
    public Optional<Integer> findManagingGroupId(String username) {
        return users.findIdByUsername(username)
                .flatMap(groups::findIdByMemberId);
    }

    /**
     * 列出用户组的所有用户名
     * @param gid 用户组id
     * @return 组中用户的用户名
     */
    public List<String> findMemberNamesByGid(Integer gid) {
        return groups.findMemberNamesById(gid);
    }

    public Optional<Group> findGroupByName(String name) {
        return groups.findByName(name);
    }

    public Optional<User> findUserByName(String username) {
        return users.findByUsername(username);
    }

    /**
     * 利用成员用户名查找所属用户组，然后返回用户组名
     * @param username 成员用户名
     * @return 所属用户组名
     */
    public Optional<String> findGroupNameByMemberName(String username) {
        return groups.findNameByMemberName(username);
    }

    ///
    // 存储类
    //   将entity中的@Transient属性转换成数据库列，并加以存储
    //   存储User之时，不存在的Group直接转null，因此需要先存Group
    ///

    private User prepare(User user) {
        return user.setPassword(encoder.encode(user.getPassword()));
    }

    // 给DataService用
    // 此时所有user和group都不存在
    void saveAll(List<User> userList, List<Group> groupList) {
        Stream<User> users = userList.stream().map(this::prepare);
        userList = this.users.saveAll(users::iterator);

        val map = userList.stream().collect(toMap(
            User::getUsername, value -> value
        ));

        Function<List<String>, Set<User>> cvt =
            list -> list != null ? list.stream().map(map::get).collect(toSet()) : null;

        Stream<Group> groups = groupList.stream().map(group ->
            group.setAdmins(cvt.apply(group.getAdminNames()))
                 .setMembers(cvt.apply(group.getMemberNames())));

        this.groups.saveAll(groups::iterator);
    }
}
