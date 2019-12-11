package com.tjh.riskfactor.service

import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder

import com.tjh.riskfactor.entity.User
import com.tjh.riskfactor.repo.UserRepository

@Service
class UserService(
    private val encoder: PasswordEncoder,
    override val repo: UserRepository
): IDBService<User>("user") {

    fun has(username: String) = repo.existsByUsername(username)

    fun find(username: String): User? = repo.findByUsername(username)

    /**
     * 确认用户是否由管理员所管理。不包含root的情况
     * @param adminId 管理员用户id
     * @param userId 用户id
     * @return 用户是否被管理员管理
     */
    fun isManaging(adminId: Int, userId: Int) = repo.managedUserIds(adminId).contains(userId)

    /**
     * 将原始密码加密
     * @param password 原始密码
     * @return 加密后密码
     */
    fun encode(password: String) = encoder.encode(password)

    /**
     * 将用户实体的密码加密
     * @param u 用户实体
     * @return 密码加密后的用户实体
     */
    fun encoded(u: User) = u.apply { password = encode(u.password) }

}
