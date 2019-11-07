package com.tjh.riskfactor.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.repo.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IDBService<User> {

    @Getter
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public boolean has(String username) {
        return repo.existsByUsername(username);
    }

    public Optional<User> find(String username) {
        return repo.findByUsername(username);
    }

    /**
     * 确认用户是否由管理员所管理。不包含root的情况
     * @param adminId 管理员用户id
     * @param userId 用户id
     * @return 用户是否被管理员管理
     */
    public boolean isManaging(Integer adminId, Integer userId) {
        return repo.isManaging(adminId, userId);
    }

    /**
     * 获取用户的组名（非显示用名）
     * @param uid 用户id
     * @return 用户组名
     */
    public Optional<String> myGroupName(Integer uid) {
        return repo.findGroupName(uid);
    }

    /**
     * 获取用户所管理的组id
     * @param uid 用户id
     * @return 管理的组id
     */
    public Optional<Integer> managedGroupId(Integer uid) {
        return repo.findManagedGroupId(uid);
    }

    /**
     * 将原始密码加密
     * @param password 原始密码
     * @return 加密后密码
     */
    public String encode(String password) {
        return encoder.encode(password);
    }

    /**
     * 将用户实体的密码加密
     * @param u 用户实体
     * @return 密码加密后的用户实体
     */
    User encoded(User u) {
        return u.setPassword(encoder.encode(u.getPassword()));
    }

    @Override
    public String getEntityName() {
        return "user";
    }
}
