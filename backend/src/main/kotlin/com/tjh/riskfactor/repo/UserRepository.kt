package com.tjh.riskfactor.repo

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.JpaRepository

import com.tjh.riskfactor.entity.User

@Repository
interface UserRepository: JpaRepository<User, Int> {

    @Query("select u from User u where u.username = :username")
    fun findByName(username: String): User?

    fun existsByUsername(username: String): Boolean

    /**
     * 根据组管理员id查询所有组内用户成员的id
     * @param adminId 组管理员id
     * @return 若[adminId]不是管理员，则返回空[List]；其他情况返回全部组成员id（包括管理员本身）
     */
    @Query("""select u.id from User a inner join User u on
          a.id = :adminId and a.admin = true and a.group.id = u.group.id""")
    fun managedUserIds(adminId: Int): List<Int>

    /**
     * 获取用户所管理的组id
     * @param uid 用户id
     * @return 管理的组id
     */
    @Query("select u.group.id from User u where u.id = :uid and u.admin = true")
    fun findManagedGroupId(uid: Int): Int?

}
