package com.tjh.riskfactor.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.repo.GroupRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GroupService implements IDBService {

    private final GroupRepository groups;

    public void drop() {
        groups.deleteAll();
    }

    /**
     * 获取用户组的所有用户的用户名
     * @param gid 用户组id
     * @return 用户组成员的用户名
     */
    public List<String> memberNames(Integer gid) {
        return groups.findMemberNamesById(gid);
    }

    /**
     * 根据id获取用户组
     * @param gid 用户组id
     * @return 用户组
     */
    public Optional<Group> groupWithId(Integer gid) {
        return groups.findById(gid);
    }

    /**
     * 根据用户组名获取用户组
     * @param name 用户组名
     * @return 用户组
     */
    public Optional<Group> groupWithName(String name) {
        return groups.findByName(name);
    }

    /**
     * 根据用户成员的用户名获取该用户所在用户组名称
     * @param member 成员用户名
     * @return 用户所在用户组名称
     */
    public Optional<String> groupNameWithMember(String member) {
        return groups.findNameByMemberName(member);
    }

    /**
     * 查找被用户管理的用户组的id
     * @param username 用户组管理员的用户名
     * @return 用户组id
     */
    public Optional<Integer> idManagedBy(String username) {
        return groups.findIdByAdminName(username);
    }

    List<Group> saveAll(Stream<Group> groups) {
        return this.groups.saveAll(groups::iterator);
    }
}
