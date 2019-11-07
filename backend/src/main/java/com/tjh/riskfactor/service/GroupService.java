package com.tjh.riskfactor.service;

import lombok.Getter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.tjh.riskfactor.entity.Group;
import com.tjh.riskfactor.repo.GroupRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService implements IDBService<Group> {

    @Getter
    private final GroupRepository repo;

    /**
     * 获取用户组的所有用户的用户名
     * @param gid 用户组id
     * @return 用户组成员的用户名
     */
    public List<String> memberNames(Integer gid) {
        return repo.findMemberNamesById(gid);
    }

    /**
     * 根据用户组名获取用户组
     * @param name 用户组名
     * @return 用户组
     */
    public Optional<Group> groupWithName(String name) {
        return repo.findByName(name);
    }

    @Override
    public String getEntityName() {
        return "group";
    }

}
