package com.tjh.riskfactor.repo

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.JpaRepository

import com.tjh.riskfactor.entity.User

@Repository
interface UserRepository: JpaRepository<User, Int> {

    fun findByUsername(username: String): User?

    fun existsByUsername(username: String): Boolean

    @Query(nativeQuery = true, value =
        "select count(*) from users a, users u where " +
            "a.id = :adminId and u.id = :userId and a.group_id = u.group_id and a.is_admin = true"
    )
    fun isManaging(adminId: Int, userId: Int): Boolean

    @Query("select u.id from User u where u.username = :username")
    fun findIdByUsername(username: String): Int?

    @Query("select g.name from User u inner join u.group g on u.id = :uid")
    fun findGroupName(uid: Int): String?

    @Query(nativeQuery = true, value =
        "select u.group_id from users u where u.id = :uid and u.is_admin = true"
    )
    fun findManagedGroupId(uid: Int): Int?

}
